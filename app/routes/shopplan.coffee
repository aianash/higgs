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

Piggyback.register('GET',     '/shopplan/all',                      shopplan.list)              .in(router)
Piggyback.register('POST',    '/shopplan/create',                   shopplan.create)            .in(router)

Piggyback.register('GET',     '/shopplan/:suid',                    shopplan.get)               .in(router)
Piggyback.register('POST',    '/shopplan/:suid',                    shopplan.cud)               .in(router)
Piggyback.register('DELETE',  '/shopplan/:suid',                    shopplan.end)               .in(router)

module.exports = router