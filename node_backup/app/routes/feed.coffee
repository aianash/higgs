path            = require 'path'
express         = require 'express'

Piggyback = require path.join(__dirname, '../middlewares/piggyback')
feed      = require path.join(__dirname, '../controllers/feed')

router = express.Router()

Piggyback.register('GET',  '/feed/common',    feed.common, authenticate: false)   .in(router)
Piggyback.register('GET',  '/feed/user',      feed.user)                          .in(router)

module.exports = router