bodyParser      = require 'body-parser'
Q               = require 'q'
path            = require 'path'
passport        = require 'passport'
express         = require 'express'
winston         = require 'winston'

settings        = require path.join(__dirname, '../settings')
logger          = require path.join(__dirname, '../utils/logger')
Piggyback       = require path.join(__dirname, '../middlewares/piggyback')

# Controller
shopplan        = require path.join(__dirname, '../controllers/shopplan')

jsonParser = bodyParser.json()

router = express.Router()

Piggyback.register('GET',     '/shopplan/all',                        shopplan.list)              .in(router)
Piggyback.register('GET',     '/shopplan/new',                        shopplan.createNew)         .in(router)

Piggyback.register('GET',     '/shopplan/:planId',                    shopplan.detail)            .in(router)
Piggyback.register('POST',    '/shopplan/:planId',                    shopplan.add)               .in(router)
Piggyback.register('DELETE',  '/shopplan/:planId',                    shopplan.remove)            .in(router)
Piggyback.register('PUT',     '/shopplan/:planId/end',                shopplan.end)               .in(router)

Piggyback.register('GET',     '/shopplan/:planId/invites',            shopplan.invites)           .in(router)

Piggyback.register('GET',     '/shopplan/:planId/store/locations',    shopplan.storeLocations)    .in(router)

Piggyback.register('GET',     '/shopplan/:planId/destinations',       shopplan.destinations)      .in(router)
Piggyback.register('POST',    '/shopplan/:planId/destinations',       shopplan.addDestinations)   .in(router)
Piggyback.register('PUT',     '/shopplan/:planId/destinations',       shopplan.updateDestinations).in(router)
Piggyback.register('DELETE',  '/shopplan/:planId/destinations',       shopplan.removeDestinations).in(router)


module.exports = router