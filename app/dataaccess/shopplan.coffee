Q     = require 'q'
_     = require 'lodash'
path  = require 'path'

NeutrinoClient  = require path.join(__dirname, '../lib/neutrino-client')

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')

Id      = require path.join(__dirname, '../utils/id')
Convert = require path.join(__dirname, '../utils/id')


### EXPOSED METHODS ###

module.exports.all = (uuid) ->
  NeutrinoClient.get (client) ->
    client.q.getShopPlans Id.forUser uuid



module.exports.get = (uuid, suid, fields) ->
  shopplanId = Id.forShopPlan uuid, suid
  fields     = fields || ['destinations', 'invites']

  NeutrinoClient.get (client) ->
    client.q.getShopPlan shopplanId, fields



# [TO DO] Add plan data (consisting for whole plan)
module.exports.new = (uuid, cud) ->
  cudShopPlan = Convert.toCUDShopPlan uuid, -1, cud  # -1 for unknown suid

  NeutrinoClient.get (client) ->
    client.q.createShopPlan Id.forUser uuid, cudShopPlan



module.exports.end = (uuid, suid) ->
  NeutrinoClient.get (client) ->
    client.q.endShopPlan Id.forShopPlan uuid, suid



module.exports.cud = (uuid, suid, cud) ->
  shopplanId  = Id.forShopPlan uuid, suid
  cudShopPlan = Convert.toCUDShopPlan uuid, suid, cud
  keepTxn     = getKeepTxn cud

  NeutrinoClient.get (client) ->
    client.q.cudShopPlan shopplanId, cudShopPlan
