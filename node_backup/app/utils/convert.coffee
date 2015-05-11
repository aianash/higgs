path = require 'path'
_    = require 'lodash'


common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')
search_ttypes   = require path.join(__dirname, '../lib/search_types')
feed_ttypes     = require path.join(__dirname, '../lib/feed_types')

Id = require path.join(__dirname, '/id')


Convert = {}


Convert.toItemTypes = (itemTypes) ->
  itemTypes = if _.isArray(itemTypes) then itemTypes else [itemTypes]
  itemTypes = _.map itemTypes, (itemType) -> shopplan_ttypes.ItemTypes[itemType.Convert.toUpperCase()]
  _.filter itemTypes, _.identity



# [NOTE] Right now only supports ids
Convert.toCatalogueItem = (catalogueItem) ->
  {stuid, cuid} = catalogueItem

  itemId       = Id.forCatalogueItem stuid, cuid
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


#######################################################################
####################### FEED DS CONVERSIONS ###########################
#######################################################################


Convert.toFeedFilter = (filter) ->
  {city, page} = filter

  location = new common_ttypes.PostalAddress {city} if city

  new feed_ttypes.FeedFilter {location, page}



###########################################################################
####################### USERS DS CONVERSIONS ###########################
###########################################################################

Convert.toLocale = (locale) ->
  common_ttypes.Locale[locale.toUpperCase()]



Convert.toGender = (gender) ->
  common_ttypes.Gender[gender.toUpperCase()]


Convert.toFacebookInfo = (info) ->
  id    = Id.forUser info.fbuid
  token = info.fbToken

  new common_ttypes.FacebookInfo {id, token}



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



Convert.toUserInfo = (info) ->
  {name, locale, gender, facebookInfo, email, timezone, avatar, isNew} = info

  name         = Convert.toUserName name
  locale       = Convert.toLocale locale
  gender       = Convert.toGender gender
  facebookInfo = Convert.toFacebookInfo facebookInfo
  avatar       = Convert.toUserAvatar avatar
  isNew        = isNew

  new common_ttypes.UserInfo {name, locale, gender, facebookInfo, email, timezone, avatar, isNew}



###########################################################################
####################### ShopPlan DS CONVERSIONS ###########################
###########################################################################


Convert.toShopPlanStoreFields = (fields) ->
  fields = if _.isArray(fields) then fields else [fields]
  fields = _.map fields, (field) -> shopplan_ttypes.ShopPlanStoreField[field]
  _.filter fields, _.identity



Convert.toShopPlanFields = (fields) ->
  fields = if _.isArray(fields) then fields else [fields]
  fields = _.map fields, (field) -> shopplan_ttypes.ShopPlanField[field]
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

  new shopplan_ttypes.Invite {friendId, shopplanId, name, avatar}



Convert.toCUDDShopPlanItems = (cud) ->
  toCatalogueItemId = (itemId) -> Id.forCatalogueItem(itemId.stuid, itemId.cuid)

  adds     = _.map cud.adds.items, toCatalogueItemId
  removals = _.map cud.removals.items, toCatalogueItemId

  new neutrino_ttypes.CUDShopPlanItems {adds, removals}



Convert.toCUDInvites = (uuid, suid, cud) ->
  adds     = _.map cud.adds.invites, Id.forUser
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
  title = cud.updates.title
  new neutrino_ttypes.CUDShopPlanMeta {title}



Convert.toCUDShopPlan = (uuid, suid, cud) ->
  meta         = Convert.toCUDShopPlanMeta cud
  destinations = Convert.toCUDDestination uuid, suid, cud
  invites      = Convert.toCUDInvites cud
  items        = Convert.toCUDDShopPlanItems cud

  new neutrino_ttypes.CUDShopPlan {meta, destinations, invites, items}



################################################
############# BUCKET DS CONVERSIONS  ###########
################################################

Convert.toBucketStoreFields = (fields) ->
  fields = if _.isArray(fields) then fields else [fields]
  fields = _.map fields, (field) -> shopplan_ttypes.BucketStoreField[field]
  _.filter fields, _.identity



Convert.toCUDBucket = (cud) ->
  adds = _.map cud.adds.itemIds, (itemId) -> Id.forCatalogueItem(itemId.stuid, itemId.cuid)
  adds = _.filter adds, _.identity
  new neutrino_ttypes.CUDBucket {adds}


################################################
############# SEARCH DS CONVERSIONS  ###########
################################################

Convert.toCatalogueSearchQuery = (query) ->
  queryText = query
  new search_ttypes.CatalogueSearchQuery {queryText}



Convert.toCatalogueSearchRequest = (uuid, sruid, query) ->
  searchId  = Id.forSearch uuid, sruid
  query     = Convert.toCatalogueSearchQuery query
  pageIndex = query.pageIndex
  pageSize  = query.pageSize

  new search_ttypes.CatalogueSearchRequest {searchId, query, pageIndex, pageSize}


module.exports = Convert