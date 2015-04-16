path            = require 'path'
express         = require 'express'

Piggyback = require path.join(__dirname, '../middlewares/piggyback')
shopplan  = require path.join(__dirname, '../controllers/shopplan')

router = express.Router()

Piggyback.register('GET',     '/shopplan/list',    shopplan.list)    .in(router)
Piggyback.register('POST',    '/shopplan/create',  shopplan.create)  .in(router)

Piggyback.register('GET',     '/shopplan/:suid',   shopplan.get)     .in(router)
Piggyback.register('POST',    '/shopplan/:suid',   shopplan.cud)     .in(router)
Piggyback.register('DELETE',  '/shopplan/:suid',   shopplan.end)     .in(router)

Piggyback.register('GET',     '/shopplan/:suid/stores', shopplan.stores) .in(router)

module.exports = router