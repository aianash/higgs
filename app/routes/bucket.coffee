path      = require 'path'
express   = require 'express'

Piggyback = require path.join(__dirname, '../middlewares/piggyback')

# Controller
bucket    = require path.join(__dirname, '../controllers/bucket')

router = express.Router()

Piggyback.register('GET',     '/bucket/stores',    bucket.stores)    .in(router)
Piggyback.register('POST',    '/bucket/cud',       bucket.cud)     .in(router)

module.exports = router