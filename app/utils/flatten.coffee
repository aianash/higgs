path = require 'path'
_    = require 'lodash'

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')


# Helper conversions
ReverseItemType = _.invert shopplan_ttypes.ItemType
ReverseLocale   = _.invert common_ttypes.Locale
ReverseGender   = _.invert common_ttypes.Gender

Flatten = {}

Flatten.locale = (locale) ->
  if _.isString locale then locale.toUpperCase()
  else if _.isNumber locale then ReverseLocale[locale]
  else null


Flatten.gender = (gender) ->
  if _.isString gender then gender.toLowerCase()
  else if _.isNumber gender then ReverseGender[gender]
  else null


Flatten.facebookInfo = (facebookInfo) ->
  fbuid   = facebookInfo.userId.uuid
  fbToken = facebookInfo.token

  facebookInfo = {fbuid, fbToken}
  _.pick facebookInfo, _.identity


Flatten.userName = (name) ->
  name = _.pick name, _.identity
  _.isEmpty name ? null : name


Flatten.userAvatar = (avatar) ->
  avatar = _.pick avatar, _.identity
  _.isEmpty avatar ? null : avatar


Flatten.storeName = (name) ->
  name = _.pick name, _.identity
  _.isEmpty name ? null : name


Flatten.postalAddress = (address) ->
  address = _.pick address, _.identity
  _.isEmpty address ? null : address


Flatten.itemType = (itemType) ->
  if _.isString itemType then itemType.toUpperCase()
  else if _.isNumber itemType then ReverseItemType[itemType]
  else null


Flatten.itemTypes = (itemTypes) ->
  itemTypes = _.map itemTypes, Flatten.itemType
  _.filter itemTypes, _.identity


Flatten.catalogueItem = (item) ->
  stuid  = item.itemId.storeId.stuid
  cuid   = item.itemId.cuid
  detail = {} # [TO DO] decoded from binary

  {stuid, cuid, detail}


Flatten.catalogueItems = (items) ->
  items = _.map items, Flatten.catalogueItem
  _.filter items, _.identity


Flatten.userInfo = (info) ->
  name         = Flatten.userName info.name
  locale       = Flatten.locale info.locale
  gender       = Flatten.gender info.gender
  facebookInfo = Flatten.facebookInfo info.facebookInfo
  email        = info.email
  timezone     = info.timezone
  avatar       = Flatten.userAvatar info.avatar
  isNew        = info.isNew

  info = {name, locale, gender, facebookInfo, email, timezone, avatar, isNew}
  _.pick info, _.identity


Flatten.friend = (friend) ->
  fruid  = friend.id.uuid
  name   = Flatten.userName friend.name
  avatar = Flatten.userAvatar avatar

  friend = {fruid, name, avatar}
  _.pick friend, _.identity


Flatten.friends = (friends) ->
  friends = _.map friends, Flatten.friend
  _.filter friends, _.identity


################ Feed Flatteners #####################

Flatten.offerPost = (offerPost) ->
  ptuid    = offerPost.postId.ptuid
  idx      = offerPost.index
  type     = 'offer'
  stuid    = offerPost.storeId.stuid
  name     = Flatten.storeName offerPost.storeName
  address  = Flatten.postalAddress offerPost.storeAddress
  title    = offerPost.offer.title
  subtitle = offerPost.offer.subtitle

  from = {stuid, name, address}

  post = {ptuid, idx, type, from, title, subtitle}
  _.pick post, _.identity



Flatten.posterAdPost = (posterAdPost) ->
  ptuid   = posterAdPost.postId.ptuid
  idx     = posterAdPost.index
  type    = 'posterAd'
  paduid  = posterAdPost.poster.paduid
  image   = posterAdPost.poster.image.link

  post = {ptuid, idx, type, paduid, image}
  _.pick post, _.identity



Flatten.feed = (feed) ->
  offers    = _.map feed.offerPosts,    Flatten.offerPost
  posterAds = _.map feed.posterAdPosts, Flattem.posterAdPost

  entries = []
  entries = entries.concat offers.concat posterAds

  entries = _.filter entries, _.identity

  feed = _.sortBy entries, 'idx'
  page = feed.page || 0

  {page, feed}


################ ShopPlan Flatteners #####################

Flatten.shopPlanId = (shopplanId) ->
  suid      = shopplanId.suid
  createdBy = shopplanId.createdBy?.uuid

  if suid? and createdBy? then {suid, createdBy}
  else null


Flatten.shopPlanStore = (store) ->
  stuid          = store.storeId.stuid
  suid           = store.destId.shopplanId.suid
  createdBy      = store.destId.shopplanId.createdBy.uuid
  dtuid          = store.destId.dtuid

  name           = Flatten.storeName store.name
  address        = Flatten.postalAddress store.address
  itemTypes      = Flatten.itemTypes store.itemTypes
  catalogueItems = Flatten.catalogueItems store.catalogueItems

  store = {stuid, suid, createdBy, dtuid, name, address, itemTypes, catalogueItems}
  _.pick store, _.identity


Flatten.shopPlanStores = (stores) ->
  stores = _.map stores, Flatten.shopPlanStore
  _.filter stores, _.identity


Flatten.destination = (destination) ->
  suid      = destination.destId.shopplanId.suid
  createdBy = destination.destId.shopplanId.createdBy.uuid
  dtuid     = destination.destId.dtuid

  address   = Flatten.postalAddress address
  numShops  = destination.numShops || -1

  destination = {suid, createdBy, dtuid, address, numShops}
  _.pick destination, _.identity


Flatten.destinations = (destinations) ->
  destinations = _.map destinations, Flatten.destination
  _.filter destinations, _.identity


Flatten.invite = (invite) ->
  fruid     = invite.friendId.uuid
  createdBy = invite.shopplanId.createdBy.uuid
  suid      = invite.shopplanId.suid
  name      = Flatten.userName invite.name
  avatar    = Flatten.userAvatar invite.avatar

  invite = {fruid, createdBy, suid, name, avatar}
  _.pick invite, _.identity


Flatten.invites = (invites) ->
  invites = _.map invites, Flatten.invite
  _.filter invites, _.identity


Flatten.shopPlan = (shopplan) ->
  createdBy    = shopplan.shopplanId.createdBy.uuid
  suid         = shopplan.shopplanId.suid
  title        = shopplan.title || "Your shopping plan"
  stores       = Flatten.shopPlanStores shopplan.stores
  destinations = Flatten.destinations destinations
  invites      = Flatten.invites invites
  isInvitation = shopplan.isInvitation || false

  shopplan = {createdBy, suid, title, stores, destinations, invites, isInvitation}

  _.pick shopplan, _.identity


Flatten.shopPlans = (shopplans) ->
  shopplans = _.map shopplans, Flatten.shopPlan
  _.filter shopplans, _.identity

################ Bucket Flatteners #######################

Flatten.bucketStore = (store) ->
  stuid          = store.stuid
  name           = Flatten.storeName store.name
  address        = Flatten.postalAddress store.address
  itemTypes      = Flatten.itemTypes store.itemTypes
  catalogueItems = Flatten.catalogueItems catalogueItems

  flattened = {stuid, name, address, itemTypes, catalogueItems}

  _.pick flattened, _.identity


Flatten.bucketStores = (stores) ->
  stores = _.map stores, Flatten.bucketStore
  _.filter stores, _.identity



module.exports = Flatten