bodyParser      = require 'body-parser'
Q               = require 'q'
path            = require 'path'
passport        = require 'passport'
winston         = require 'winston'
Thrift          = require 'thrift'

dataaccess_path = path.join(__dirname, '../dataaccess')

urlencodedParser = bodyParser.urlencoded(extended: true)
jsonParser       = bodyParser.json()
authenticate     = passport.authenticate ['bearer'], {session: false}


logger          = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.ShopPlan     = require path.join(dataaccess_path, 'shopplan')


getUserPlans = (req, res) ->
  uuid = req.user.id

  da.ShopPlan.all(req.user.id)
    .then (plans) -> res.send plans
    .catch (err) ->
      logger.log('error', 'Error getting all plans of user', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



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


updateShopPlan = (req, res) ->
  da.ShopPlan.update(req.user.id, req.params.planId, req.body)
    .then (success) -> res.send success
    .catch (err) ->
      logger.log('error', 'Error getting plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



exports.updateShopPlan  = updateShopPlan
exports.getShopPlan     = getShopPlan
exports.getUserPlans    = getUserPlans