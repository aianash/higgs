bodyParser      = require 'body-parser'
Q               = require 'q'
path            = require 'path'
passport        = require 'passport'
express         = require 'express'
winston         = require 'winston'

controller_path = __dirname + '/../controllers'

settings        = require __dirname + '/../settings'
logger          = require __dirname + '/../utils/logger'
Piggyback       = require path.join(__dirname, '../middlewares/piggyback')

router = express.Router()

shopplan = require controller_path + '/shopplan'

jsonParser = bodyParser.json()

Piggyback.register('GET',  '/shopplan/all',                   shopplan.getUserPlans)      .in(router)
Piggyback.register('GET',  '/shopplan/:planId/detail',        shopplan.getShopPlan)       .in(router)
Piggyback.register('POST', '/shopplan/:planId/add',           shopplan.addToShopPlan)     .in(router)
Piggyback.register('POST', '/shopplan/:planId/remove',        shopplan.removeFromShopPlan).in(router)
Piggyback.register('POST', '/shopplan/:planId/end',           shopplan.endPlan)           .in(router)
Piggyback.register('GET',  '/shopplan/:planId/map/locations', shopplan.mapLocations)      .in(router)

module.exports = router