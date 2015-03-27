path = require 'path'

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')


forUser = module.exports.forUser = (uuid) -> new common_ttypes.UserId {uuid}


forShopPlan = module.exports.forShopPlan = (uuid, suid) ->
  createdBy = forUser uuid
  new shopplan_ttypes.ShopPlanId {createdBy, suid}



forDestination = module.exports.forDestination = (uuid, suid, duid) ->
  shopplanId = forShopPlan uuid, suid
  new shopplan_ttypes.DestinationId {shopplanId, duid}



forStore = module.exports.forStore = (stuid) ->
  new common_ttypes.StoreId {stuid}



forCatalogueItem = module.exports.forCatalogueItem = (stuid, cuid) ->
  storeId = forStore stuid
  new common_ttypes.CatalogueItemId {cuid, storeId}

