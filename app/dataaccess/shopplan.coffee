path  = require 'path'

NeutrinoClient  = require path.join(__dirname, '../lib/neutrino-client')

Id              = require path.join(__dirname, '../utils/id')
Convert         = require path.join(__dirname, '../utils/convert')


### EXPOSED METHODS ###

module.exports.invitedPlans = (uuid, fields) ->
  NeutrinoClient.get (client) ->
    client.q.getInvitedShopPlans Id.forUser(uuid), Convert.toShopPlanFields(fields)



module.exports.ownPlans = (uuid, fields) ->
  NeutrinoClient.get (client) ->
    client.q.getOwnShopPlans Id.forUser(uuid), Convert.toShopPlanFields(fields)



module.exports.get = (uuid, suid, fields) ->
  shopplanId = Id.forShopPlan uuid, suid

  NeutrinoClient.get (client) ->
    client.q.getShopPlan shopplanId, Convert.toShopPlanFields(fields)



module.exports.new = (uuid, cud) ->
  cudShopPlan = Convert.toCUDShopPlan uuid, -1, cud  # -1 for unknown suid

  NeutrinoClient.get (client) ->
    client.q.createShopPlan Id.forUser(uuid), cudShopPlan



module.exports.end = (uuid, suid) ->
  NeutrinoClient.get (client) ->
    client.q.endShopPlan Id.forShopPlan uuid, suid



module.exports.cud = (uuid, suid, cud) ->
  shopplanId  = Id.forShopPlan uuid, suid
  cudShopPlan = Convert.toCUDShopPlan uuid, suid, cud

  NeutrinoClient.get (client) ->
    client.q.cudShopPlan shopplanId, cudShopPlan



module.exports.stores = (uuid, suid, fields) ->
  shopplanId = Id.forShopPlan uuid, suid
  fields = Convert.toShopPlanStoreFields fields

  NeutrinoClient.get (client) ->
    client.q.getShopPlanStores shopplanId, fields