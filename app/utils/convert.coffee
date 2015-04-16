path = require 'path'
_    = require 'lodash'


common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')

Id = require path.join(__dirname, '/id')


Convert = {}

Convert.toCatalogueItemId = (stuid, cuid) ->
  storeId = Convert.toStoreId stuid
  new common_ttypes.CatalogueItemId {storeId, cuid}


Convert.toItemTypes = (itemTypes) ->
  itemTypes = _.isArray itemTypes ? itemTypes : [itemTypes]
  itemTypes = _.map itemTypes, (itemType) -> shopplan_ttypes.ItemTypes[itemType.Convert.toUpperCase()]
  _.filter itemTypes, _.identity


# [NOTE] Right now only supports ids
Convert.toCatalogueItem = (catalogueItem) ->
  {stuid, cuid} = catalogueItem

  itemId       = Convert.toCatalogueItemId stuid, cuid
  serializerId = new common_ttypes.SerializerId {sid: 'unknown', stype: common_ttypes.SerializerType.UNKNOWN}
  stream       = catalogueItem.stream || ''  # [TO DO] encode and then set

  new common_ttypes.SerializedCatalogueItem {itemId, serializerId, stream}


Convert.toCatalogueItems = (catalogueItems) ->
  catalogueItems = _.map catalogueItems, Convert.toCatalogueItem
  _.filter catalogueItems, _.identity



Convert.toGPSLocation = (gpsLoc) ->
  {lat, lng} = gpsLoc
  new common_ttypes.GPSLocation {lat, lng}


Convert.toPostalAddress = (address) ->
  {gpsLoc, title, short, full, pincode, country, city} = address

  gpsLoc = Convert.toGPSLocation gpsLoc

  new shopplan_ttypes.PostalAddress {gpsLoc, title, short, full, pincode, country, city}


Convert.toStoreName = (name) ->
  {full, handle} = name
  new common_ttypes.StoreName {full, handle}


###########################################################################
####################### USERS DS CONVERSIONS ###########################
###########################################################################


Convert.toFriendListFilter = (filter) ->
  {location} = filter

  location = Convert.toPostalAddress location

  new neutrino_ttypes.FriendListFilter {location}



Convert.toUserName = (name) ->
  {first, last, handle} = name
  new common_ttypes.UserName {first, last, handle}



Convert.toUserAvatar = (avatar) ->
  {small, medium, large} = avatar
  new common_ttypes.UserAvatar {small, medium, large}



Convert.toFriend = (friend) ->
  {uuid, name, avatar, inviteStatus} = friend

  id           = Id.forUser uuid
  name         = Convert.toUserName name
  avatar       = Convert.toUserAvatar avatar
  inviteStatus = shopplan_ttypes.InviteStatus[inviteStatus]

  new shopplan_ttypes.Friend {id, name, avatar, inviteStatus}



Convert.toUserInfo = () ->


###########################################################################
####################### ShopPlan DS CONVERSIONS ###########################
###########################################################################


Convert.toShopPlanStoreFields = (fields) ->
  fields = _.isArray fields ? fields : [fields]
  fields = _.map fields, (field) -> shopplan_ttypes.ShopPlanStoreField[field.Convert.toUpperCase()]
  _.filter fields, _.identity



Convert.toShopPlanFields = (fields) ->
  fields = _.isArray fields ? fields : [fields]
  fields = _.map fields, (field) -> shopplan_ttypes.ShopPlanField[field.Convert.toUpperCase()]
  _.filter fields, _.identity



Convert.toShopPlanStore = (uuid, suid, store) ->
  {stuid, dtuid, name, address, itemTypes, catalogueItems} = store

  storeId        = Id.forStore stuid
  destId         = Id.forDestination uuid, suid, dtuid
  name           = Convert.toStoreName name
  address        = Convert.toPostalAddress address
  itemTypes      = Convert.toItemTypes itemTypes
  catalogueItems = Convert.toCatalogueItems catalogueItems

  new shopplan_ttypes.ShopPlanStore {storeId, destId, name, address, itemTypes, catalogueItems}


Convert.toDestination = (uuid, suid, dest) ->
  destId   = Id.forDestination uuid, suid, dest.dtuid
  address  = Convert.toPostalAddress dest.address
  numShops = dest.numShops || -1

  new shopplan_ttypes.Destination {destId, address, numShops}



Convert.toInvite = (uuid, suid, invite) ->
  friendId   = Id.forUser invite.fruid
  shopplanId = Id.forShopPlan uuid, suid
  name       = Convert.toUserName invite.name
  avatar     = Convert.toUserAvatar invite.avatar

  new shopplan_types.Invite {friendId, shopplanId, name, avatar}



Convert.toCUDDShopPlanStores = (uuid, suid, cud) ->
  toShopPlanStore = _.partial Convert.toShopPlanStore, uuid, suid

  adds =
    _.map cud.adds.stores, (store) ->
      items = _.map store.catalogueItemIds, (cuid) -> {stuid: store.stuid, cuid}
      store.catalogueItems = store.catalogueItems || items
      store

  adds     = _.map adds, toShopPlanStore
  removals = _.map cud.removals.stores, Id.forStore

  new neutrino_ttypes.CUDShopPlanStores {adds, removals}



Convert.toCUDInvites = (uuid, suid, cud) ->
  toInvite = _.partial Convert.toInvite, uuid, suid

  adds     = _.map cud.adds.invites, toInvite
  removals = _.map cud.removals.invites, Id.forUser

  new neutrino_ttypes.CUDInvites {adds, removals}


Convert.toCUDDestination = (uuid, suid, cud) ->
  toDestination   = _.partial Convert.toDestination, uuid, suid
  toDestinationId = _.partial Id.forDestination, uuid, suid

  adds     = _.map cud.adds.destinations, toDestination
  updates  = _.map cud.updates.destinations, toDestination
  removals = _.map cud.removals.destinations, toDestinationId

  new neutrino_ttypes.CUDDestination {adds, updates, removals}



Convert.toCUDShopPlanMeta = (meta) ->
  new neutrino_ttypes.CUDShopPlanMeta {title: meta.title}



Convert.toCUDShopPlan = (uuid, suid, cud) ->
  meta         = Convert.toCUDShopPlanMeta {title: cud.title}
  destinations = Convert.toCUDDestination uuid, suid, cud
  invites      = Convert.toCUDInvites cud
  stores       = Convert.toCUDDShopPlanStores uuid, suid, cud

  new neutrino_ttypes.CUDShopPlan {meta, destinations, invites, stores}



################################################
############# BUCKET DS CONVERSIONS  ###########
################################################

Convert.toBucketStoreFields = (fields) ->
  fields = _.isArray fields ? fields : [fields]
  fields = _.map fields, (field) -> shopplan_ttypes.BucketStoreField[field.Convert.toUpperCase()]
  _.filter fields, _.identity


Convert.toBucketStore = (store) ->
  {stuid, name, address, itemTypes, catalogueItems} = store

  storeId        = Id.forStore stuid
  name           = Convert.toStoreName name
  address        = Convert.toPostalAddress address
  itemTypes      = Convert.toItemTypes itemTypes ##
  catalogueItems = Convert.toCatalogueItems catalogueItems ##

  new shopplan_ttypes.BucketStore {storeId, name, address, itemTypes, catalogueItems}



Convert.toCUDBucket = (cud) ->
  adds = _.map cud.creates.stores, Convert.toBucketStore
  adds = _.filter adds, _.identity
  new shopplan_ttypes.CUDBucket {adds}


module.exports = Convert