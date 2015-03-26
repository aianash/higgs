bodyParser      = require 'body-parser'
Q               = require 'q'
path            = require 'path'
passport        = require 'passport'
winston         = require 'winston'
Thrift          = require 'thrift'

dataaccess_path = path.join(__dirname, '../dataaccess')

urlencodedParser = bodyParser.urlencoded(extended: true)
authenticate     = passport.authenticate ['bearer'], {session: false}


logger          = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.ShopPlan      = require path.join(dataaccess_path, 'shopplan')


getUserPlans = (req, res) ->
  uuid = req.user.id

  res.send {message: 'coming soon'}


getShopPlan = (req, res) ->
  da.ShopPlan.get(req.user.id, req.params.planId)
    .then (plan) -> res.send plan
    .catch (err) ->
      logger.log('error', 'Error getting plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



exports.getShopPlan = [
  urlencodedParser,
  authenticate,
  getShopPlan
]

exports.getUserPlans = [
  urlencodedParser,
  authenticate,
  getUserPlans
]