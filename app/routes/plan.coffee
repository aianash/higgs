bodyParser = require 'body-parser'
Q = require 'q'

controller_path = __dirname + '/../controllers'
models_path     = __dirname + '/../models'

settings        = require __dirname + '/../settings'
logger          = require __dirname + '/../utils/logger'
winston         = require 'winston'


module.exports = (app) ->

  urlencodedParser = bodyParser.urlencoded(extended: true)

  Plans = require controller_path + '/plans'

  app.get '/apiv1/plans/get', urlencodedParser, Plans.getUserPlans