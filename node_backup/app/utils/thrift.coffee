Q       = require 'q'
_       = require 'lodash'



# Return a promised version of apis in
# the client
module.exports.getQPromisedOps = (client) ->
  functions = _.functions client

  # A hack to find out api names in the client
  apis = functions.filter (f) ->
    (f.indexOf('send_') is -1) &&
      (f.indexOf('recv_') is -1) &&
      (f isnt 'seqid') &&
      (f isnt 'pClass') &&
      (f isnt 'new_seqid')

  q = {}

  for api in apis
    q[api] = Q.nbind client[api], client

  q
