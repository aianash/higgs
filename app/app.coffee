express       = require 'express'
passport      = require 'passport'
compress      = require 'compression'
errorHandler  = require 'errorhandler'
morgan        = require 'morgan'
bodyParser    = require 'body-parser'
methodOverride = require 'method-override'

settings      = require __dirname + '/settings'
logger        = require __dirname + '/utils/logger'
winston       = require 'winston'

oauth2        = require __dirname + '/oauth2'


env = settings.get('NODE_ENV')
port = settings.get('server:port')
host = settings.get('server:host')

app = express()
app.settings.env = env

jsonParser = bodyParser.json()
urlencodedParser = bodyParser.urlencoded(extended: true)

# app.set 'port', port
app.set 'showStackError', true

app.enable 'case sensitive routing'
app.enable 'strict routing'

app.use compress()
app.use methodOverride()
app.use morgan('combined')
app.use errorHandler()

app.use passport.initialize()
app.use passport.session()

app.locals.title = 'Higgs'

# Passport configurations
require __dirname + '/auth'

app.post  '/oauth/token', jsonParser, oauth2.token


# Add api routes file name from the routes directory
# [only those that needs authentication]
apiRoutes = ['plan']

for route in apiRoutes
  require(__dirname + "/routes/#{route}")(app)


server = app.listen port, host, ->
  host = server.address().address
  port = server.address().port

  logger.log 'info', 'Server started ...', {port: port, host: host}