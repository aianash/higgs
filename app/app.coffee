express       = require 'express'
compress      = require 'compression'
errorHandler  = require 'errorhandler'
morgan        = require 'morgan'
bodyParser    = require 'body-parser'
methodOverride = require 'method-override'

settings      = require __dirname + '/settings'
logger        = require __dirname + '/utils/logger'
winston       = require 'winston'


env = settings.get('NODE_ENV')
p
ort = settings.get('server:port')
host = settings.get('server:host')

app = express()
app.settings.env = env

# app.set 'port', port
app.set 'showStackError', true

app.enable 'case sensitive routing'
app.enable 'strict routing'

app.use compress()

jsonParser = bodyParser.json()
urlencodedParser = bodyParser.urlencoded(extended: true)

app.use methodOverride()

app.locals.title = 'Higgs'
app.use morgan('combined')
app.use errorHandler()



# Add api routes file name from the routes directory
# [only those that needs authentication]
apiRoutes = ['plan']

ensureAuthenticated = (req, res, next) ->
  next() # [TO DO] check if authenticated

app.all '*', ensureAuthenticated

for route in apiRoutes
  require(__dirname + "/routes/#{route}")(app)


server = app.listen port, host, ->
  host = server.address().address
  port = server.address().port

  logger.log 'info', 'Server started ...', {port: port, host: host}