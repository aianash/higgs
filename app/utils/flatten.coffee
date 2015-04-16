path = require 'path'
_    = require 'lodash'

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')



# Helper conversions
ReverseItemType = _.invert shopplan_ttypes.ItemType

Flatten = {}

Flatten.storeName = (name) ->
  name = _.pick name, _.identity
  _.isEmpty name ? null : name


Flatten.address = (address) ->
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


Flatten.bucketStore = (store) ->
  stuid          = store.stuid
  name           = Flatten.storeName store.name
  address        = Flatten.address store.address
  itemTypes      = Flatten.itemTypes store.itemTypes
  catalogueItems = Flatten.catalogueItems catalogueItems

  flattened = {stuid, name, address, itemTypes, catalogueItems}

  _.pick flattened, _.identity


Flatten.bucketStores = (stores) ->
  stores = _.map stores, bucketStore
  _.filter stores, _.identity



module.exports = Flatten