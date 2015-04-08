path = require 'path'

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')

Id = require path.join(__dirname, '/id')


toGPSLocation = module.exports.toGPSLocation = (gpsLoc) ->
  {lat, lng} = gpsLoc
  new common_ttypes.GPSLocation {lat, lng}



toPostalAddress = module.exports.toPostalAddress = (address) ->
  {gpsLoc, title, short, full, pincode, country, city} = address

  gpsLoc = toGPSLocation gpsLoc

  new shopplan_ttypes.PostalAddress {gpsLoc, title, short, full, pincode, country, city}



toDestination = module.exports.toDestination = (uuid, suid, dest) ->
  destId  = Id.forDestination uuid, suid, dest.dtuid
  address = toPostalAddress dest.address

  new shopplan_ttypes.Destination {destId, address}



toCUDDestination = module.exports.toCUDDestination = (uuid, suid, cud) ->
  toDestination   = _.partial(toDestination, uuid, suid)
  toDestinationId = _.partial(Id.forDestination, uuid, suid)

  creates = _.map(cud.creates.destinations, toDestination)
  updates = _.map(cud.updates.destinations, toDestination)
  deletes = _.map(cud.deletes.destinations, toDestinationId)

  new neutrino_ttypes.CUDDestination {creates, updates, deletes}



toUserName = module.exports.toUserName = (name) ->
  {first, last, handle} = name
  new common_ttypes.UserName {first, last, handle}



toUserAvatar = module.exports.toUserAvatar = (avatar) ->
  {small, medium, large} = avatar
  new common_ttypes.UserAvatar {small, medium, large}



toFriend = module.exports.toFriend = (friend) ->
  {uuid, name, avatar, inviteStatus} = friend

  id           = Id.forUser uuid
  name         = toUserName name
  avatar       = toUserAvatar avatar
  inviteStatus = shopplan_ttypes.InviteStatus[inviteStatus]

  new shopplan_ttypes.Friend {id, name, avatar, inviteStatus}



toCUDInvites = module.exports.toCUDInvites = (cud) ->
  adds    = _.map(cud.creates.invites, toFriend)
  removes = _.map(cud.deletes.invites, Id.forUser)



  # [TO DO] add item types
toDStore = module.exports.toDStore = (uuid, suid, dstore) ->
  {stuid, name, address, dtuid} = dstore

  storeId = toStoreId stuid
  name    = toStoreName name
  address = toPostalAddress address
  destId  = Id.forDestination uuid, suid, dtuid

  new shopplan_ttypes.DStore {storeId, name, address, destId}



toCUDDStores = module.exports.toCUDDStores = (uuid, suid, cud) ->
  toDStore = _.partial(toDStore, uuid, suid)

  adds    = _.map(cud.creates.dstores, toDStore)
  removes = _.map(cud.creates.dstores, Id.forStore)

  new neutrino_ttypes.CUDDStores {adds, removes}



toCUDShopPlan = module.exports.toCUDShopPlan = (uuid, suid, cud) ->

  destinations = toCUDDestination uuid, suid, cud
  invites      = toCUDInvites cud
  dstores      = toCUDDStores uuid, suid, cud

  new neutrino_ttypes.CUDShopPlan {destinations, invites, dstores}