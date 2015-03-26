Q     = require 'q'
path  = require 'path'

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')


module.exports.get = (userId, planId) ->
  uuid = userId
  suid = planId

  createdBy = new goshoplane.common.UserId({uuid})
  shopplanId = new neutrino.shopplan.ShopPlanId({createdBy, suid})


  stuid = 10109288939399
  storeId = new goshoplane.common.StoreId({stuid})
  cuid = 19299920003
  itemId = new goshoplane.common.CatalogueItemId({cuid, storeId})

  sid = 'clothing-id'
  type = common_ttypes.SerializerType.MSGPCK
  serializerId = new goshoplane.common.SerializerId({sid, type})

  stream = 'serialized bytes of the item defination'
  catalgoueItem = new goshoplane.common.SerializedCatalogueItem({itemId, serializerId, stream})

  collection = [catalgoueItem]
  name = 'levis showroom'
  address = new goshoplane.common.PostalAddress({title: 'kormangla 6th cross'})
  store = new neutrino.shopplan.Store({storeId, name, collection, address})
  stores = [store]


  duid = 9299388320000229
  destId = new neutrino.shopplan.DestinationId({shopplanId, duid})
  order = 1
  destAddr = new goshoplane.common.PostalAddress({title: 'kormangla'})
  destination = new neutrino.shopplan.Destination({destId, order, stores, address: destAddr})
  destinations = [destination]


  id = 839200029299930
  name = 'einstein'
  avatar = 'https://imagizer.imageshack.us/148x163f/673/8QZyNs.jpg'
  inviteStatus = shopplan_ttypes.InviteStatus.ACCEPTED

  friend = new neutrino.shopplan.Friend({id, name, avatar, inviteStatus})
  friends = [friend]

  title = 'Shopping for party'
  plan = new neutrino.shopplan.ShopPlan({shopplanId, title, destinations, friends})

  Q(plan)