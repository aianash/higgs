Q               = require 'q'
path            = require 'path'
winston         = require 'winston'


logger          = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.ShopPlan     = require path.join(__dirname, '../dataaccess/shopplan')


list = (req, res) ->
  da.ShopPlan.all req.user.uuid
    .then (plans) -> res.send plans
    .catch (err) ->
      logger.log('error', 'Error getting all plans of user', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



create = (req, res) ->
  da.ShopPlan.new req.user.uuid
    .then (plan) -> res.send plan
    .catch (err) ->
      logger.log('error', 'Error creating new plan for user', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



get = (req, res) ->
  fields = req.query.fields

  da.ShopPlan.get req.user.uuid, req.params.suid, fields
    .then (plan) -> res.send plan
    .catch (err) ->
      logger.log('error', 'Error getting plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



cud = (req, res) ->
  da.ShopPlan.cud req.user.uuid, rep.params.suid, req.body
    .then (plan) -> res.send plan # plan with summary (title, invites, destinations)
    .catch (err) ->
      logger.log('error', 'Error performing cud on plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



end = (req, res) ->
  da.ShopPlan.end req.user.uuid, req.params.suid
    .then (success) -> res.send success
    .catch (err) ->
      logger.log('error', 'Couldnt end the plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()


# Handlers for request
exports.list      = list
exports.create    = create
exports.get       = get
exports.cud       = cud
exports.end       = end