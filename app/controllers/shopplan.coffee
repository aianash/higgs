Q               = require 'q'
path            = require 'path'
winston         = require 'winston'


logger          = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.ShopPlan     = require path.join(__dirname, '../dataaccess/shopplan')


list = (req, res) ->
  da.ShopPlan.all req.user.id
    .then (plans) -> res.send plans
    .catch (err) ->
      logger.log('error', 'Error getting all plans of user', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



createNew = (req, res) ->
  da.ShopPlan.new req.user.id
    .then (plan) -> res.send plan
    .catch (err) ->
      logger.log('error', 'Error creating new plan for user', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



detail = (req, res) ->
  da.ShopPlan.get req.user.id, req.params.planId
    .then (plan) -> res.send plan
    .catch (err) ->
      logger.log('error', 'Error getting plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



end = (req, res) ->
  da.ShopPlan.end req.user.id, req.params.planId
    .then (success) -> res.send success
    .catch (err) ->
      logger.log('error', 'Couldnt end the plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



add = (req, res) ->
  da.ShopPlan.addToShopPlan req.user.id, req.params.planId, req.body
    .then (success) -> res.send success
    .catch (err) ->
      logger.log('error', 'Error adding to plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



remove = (req, res) ->
  da.ShopPlan.removeFromShopPlan req.user.id, req.params.planId, req.body
    .then (success) -> res.send success
    .catch (err) ->
      logger.log('error', 'Error removing from plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



invites = (req, res) ->
  da.ShopPlan.getInvitedUsers req.user.id, req.params.planId
    .then (users) -> res.send users
    .catch (err) ->
      logger.log('error', 'Error getting invited users', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



friends = (req, res) ->
  da.ShopPlan.getFriendsForInvite req.user.id, req.params.planId, req.body
    .then (friends) -> res.send friends
    .catch (err) ->
      logger.log('error', 'Error getting friends to invite for plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



storeLocations = (req, res) ->
  da.ShopPlan.getStoreLocations req.user.id, req.params.planId
    .then (locations) -> res.send locations
    .catch (err) ->
      logger.log('error', 'Error getting store locations', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



destinations = (req, res) ->
  da.ShopPlan.getDestinations req.user.id, req.params.planId
    .then (destinations) -> res.send destinations
    .catch (err) ->
      logger.log('error', 'Error get destinations', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



addDestinations = (req, res) ->
  da.ShopPlan.addDestinations req.user.id, req.params.planId, req.body
    .then (destinations) -> res.send destinations
    .catch (err) ->
      logger.log('error', 'Error adding destinations', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



updateDestinations = (req, res) ->
  da.ShopPlan.updateDestinations req.user.id, req.params.planId, req.body
    .then (success) -> res.send success
    .catch (err) ->
      logger.log('error', 'Error updating destinations', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



removeDestinations = (req, res) ->
  da.ShopPlan.removeDestinations req.user.id, req.params.planId, req.body
    .then (success) -> res.send success
    .catch (err) ->
      logger.log('error', 'Error removing destinations', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



# Handlers for request
exports.list                = list
exports.createNew           = createNew
exports.detail              = detail
exports.add                 = add
exports.remove              = remove
exports.end                 = end
exports.invites             = invites
exports.friends             = friends
exports.storeLocations      = storeLocations
exports.destinations        = destinations
exports.addDestinations     = addDestinations
exports.updateDestinations  = updateDestinations
exports.removeDestinations  = removeDestinations