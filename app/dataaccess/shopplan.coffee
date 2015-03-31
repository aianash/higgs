Q     = require 'q'
_     = require 'lodash'
path  = require 'path'

NeutrinoClient  = require path.join(__dirname, '../lib/neutrino-client')

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')

Id = require path.join(__dirname, '../utils/id')



createModifyShopPlanReq = (modify) ->
  _.defaults addition, stores: [], items: [], invites: []

  {stores, items, invites} = addition

  stores  = _.map stores,  (stuid) -> Id.forStore stuid
  items   = _.map items,   (item)  -> Id.forCatalogueItem item.stuid, item.cuid
  invites = _.map invites, (uuid)  -> Id.forUser uuid

  new neutrino_ttypes.ModifyShopPlanReq {stores, items, invites}



createFriendListFilter = (filter) ->
  {city} = filter
  location = new common_ttypes.PostalAddress {city}

  new neutrino_ttypes.FriendListFilter {location}


### EXPOSED METHODS ###


module.exports.all = (uuid) ->
  NeutrinoClient.get (client) ->
    client.q.getShopPlansFor Id.forUser uuid



module.exports.get = (uuid, suid) ->
  NeutrinoClient.get (client) ->
    client.q.getShopPlan Id.forShopPlan uuid, suid



module.exports.new = (uuid) ->
  NeutrinoClient.get (client) ->
    client.q.newShopPlanFor Id.forUser uuid



module.exports.end = (uuid, suid) ->
  NeutrinoClient.get (client) ->
    client.q.endShopPlan Id.forShopPlan uuid, suid



module.exports.addToShopPlan = (uuid, suid, addition) ->
  shopplanId = Id.forShopPlan uuid, suid
  addReq = createModifyShopPlanReq addition

  NeutrinoClient.get (client) ->
    client.q.addToShopPlan shopplanId, addReq



module.exports.removeFromShopPlan = (uuid, suid, removals) ->
  shopplanId = Id.forShopPlan uuid, suid
  removeReq = createModifyShopPlanReq removals

  NeutrinoClient.get (client) ->
    client.q.removeFromShopPlan shopplanId, removeReq



module.exports.getInvitedUsers = (uuid, suid) ->
  NeutrinoClient.get (client) ->
    client.q.getInvitedUsers Id.forShopPlan uuid, suid



module.exports.getFriendsForInvite = (uuid, suid, filter) ->
  filter = createFriendListFilter filter

  NeutrinoClient.get (client) ->
    client.q.getFriendsForInvite Id.forShopPlan(uuid, suid), filter



module.exports.getStoreLocations = (uuid, suid) ->
  NeutrinoClient.get (client) ->
    client.q.getStoreLocations Id.forShopPlan uuid, suid



module.exports.getDestinations = (uuid, suid) ->
  NeutrinoClient.get (client) ->
    client.q.getDestinations Id.forShopPlan uuid, suid



module.exports.addDestinations = (uuid, suid, additions) ->
  unless _.isArray additions then return Q.reject new TypeError('additions should be an array of addition for earch duid')

  shopplanId = Id.forShopPlan uuid, suid

  toAddReq = (addition) ->
    {lat, lng, order} = addition
    location = new common_ttypes.GPSLocation({lat, lng}) if lat and lng

    new neutrino_ttypes.AddDestinationReq({location, order})

  addReqs = _.map additions, toAddReq

  NeutrinoClient.get (client) ->
    client.q.addDestinations shopplanId, addReqs



module.exports.updateDestinations = (uuid, suid, updates) ->
  unless _.isArray updates then return Q.reject new TypeError('updates should be an array of updates for each duid')

  toUpdateReq = (update) ->
    destId = Id.forDestination uuid, suid, update.duid
    {lat, lng, order} = update

    location = new common_ttypes.GPSLocation({lat, lng}) if lat and lng

    new neutrino_ttypes.UpdateDestinationReq({destId, location, order})


  updateReqs = _.map updates, toUpdateReq

  NeutrinoClient.get (client) ->
    client.q.updateDestinations updateReqs



module.exports.removeDestinations = (uuid, suid, duids) ->
  unless _.isArray duids then return Q.reject new TypeError('duids should be an array of duid')

  toDestinationId = _.partial Id.forDestination, uuid, suid
  destIds = _.map duids, toDestinationId

  NeutrinoClient.get (client) ->
    client.q.removeDestinations destIds