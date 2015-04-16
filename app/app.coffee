express         = require 'express'
passport        = require 'passport'
compress        = require 'compression'
errorHandler    = require 'errorhandler'
morgan          = require 'morgan'
bodyParser      = require 'body-parser'
methodOverride  = require 'method-override'
winston         = require 'winston'

settings      = require __dirname + '/settings'
logger        = require __dirname + '/utils/logger'

oauth2        = require __dirname + '/oauth2'


env  = settings.get('NODE_ENV')
port = settings.get('server:port')
host = settings.get('server:host')

app = express()
app.settings.env = env

jsonParser       = bodyParser.json()
urlencodedParser = bodyParser.urlencoded(extended: true)

app.set 'showStackError', true

app.enable 'case sensitive routing'
app.enable 'strict routing'

app.use compress()
app.use methodOverride()
app.use morgan('combined')
app.use errorHandler()


app.locals.title = 'Higgs'



### AUTHENTICATION SETUP ###

# App uses passport for authentication
app.use passport.initialize()


# Requiring auth file adds following authentication strategy
# - Higgs FB Authentication strategy - used when client with FB accessToken
#                requests for Higgs oAuth Token
#
# - Bearer startegy - used when client with Higgs accessToken calls APIs
#
# [For more detail on using authentication look into the file]
require __dirname + '/auth'


# App uses this endpoint to get a Higgs token
# which is passed with further API requests requiring
# authentication
app.post  '/oauth/token', jsonParser, oauth2.token





# API routes to be added to the app
apiRoutes  = ['shopplan', 'feed', 'user', 'bucket']

apiVersion = settings.get('api:version')

apiPrefix  = '/apiv' + apiVersion

for route in apiRoutes
  router = require(__dirname + "/routes/#{route}")
  app.use(apiPrefix, router)


logger.log 'info', 'Starting server ...', {port, host}

server = app.listen port, host, ->
  host = server.address().address
  port = server.address().port

  logger.log 'info', 'Server started', {port: port, host: host}