thrift  = require 'thrift'
winston = require 'winston'
Q       = require 'q'
path    = require 'path'
_       = require 'lodash'

generic_pool = require 'generic-pool'


Neutrino        = require path.join(__dirname, 'Neutrino')
common_ttypes   = require path.join(__dirname, 'common_types')
neutrino_ttypes = require path.join(__dirname, 'neutrino_types')
shopplan_ttypes = require path.join(__dirname, 'shopplan_types')

logger      = require path.join(__dirname, '../utils/logger')
settings    = require path.join(__dirname, '../settings')
ThriftUtils = require path.join(__dirname, '../utils/thrift')


nhost = settings.get('neutrino:host')
nport = settings.get('neutrino:port')

max_connection = settings.get('neutrino:max_connection')
min_connection = settings.get('neutrino:min_connection')




# Create a connection pool for Neutrino thrift connections
pool = generic_pool.Pool(
  name: 'neutrino',
  create: (callback) ->
    try
      connection = thrift.createConnection(nhost, nport, transport: thrift.TFramedTransport)
      client     = thrift.createClient(Neutrino, connection)

      client.connection = connection
      client.q = ThriftUtils.getQPromisedOps client

      connection.on 'error', (err) ->
        logger.log 'error','Connection error with message ', err.message, winston.exception.getTrace(err)
        callback err

      callback null, client
    catch err
      logger.log 'error', 'While create connection and client', err.message, winston.exception.getTrace(err)
      callback err

  destroy: (client) ->
    client.connection.end()

  max: max_connection
  min: min_connection
  idleTimeoutMillis: 300000
)


# Converting acquire to Promise based method
pool.acquireAsync = Q.nbind pool.acquire, pool



# get a new client's Promise and use it
# Caller has to manually release the client
# after use
getP = module.exports.getP = ->
  pool.acquireAsync()


# Here the caller doesnt need to
# release the client, but has to
# return (preferrably promise)
get = module.exports.get = (fn) ->
  getP().then (client) ->
    Q(fn(client)).finally -> release client


# releas the client back to pool once done
release = module.exports.release = (client) ->
  pool.release(client)