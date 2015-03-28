bodyParser      = require 'body-parser'
Q               = require 'q'
path            = require 'path'
passport        = require 'passport'
express         = require 'express'
winston         = require 'winston'

settings        = require __dirname + '/../settings'
logger          = require __dirname + '/../utils/logger'
Piggyback       = require path.join(__dirname, '../middlewares/piggyback')

router = express.Router()

feed = require path.join(__dirname, '../controllers/feed')

jsonParser = bodyParser.json()

Piggyback.register('GET',  '/feed/common',    feed.common, authenticate: false)   .in(router)
Piggyback.register('GET',  '/feed/user',      feed.user)                          .in(router)


module.exports = router