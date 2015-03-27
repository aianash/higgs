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

feeds = require controller_path + '/feeds'

jsonParser = bodyParser.json()

Piggyback.register('POST',  '/feeds/user',    feeds.user, authenticate: false)   .in(router)
Piggyback.register('POST',  '/feeds/common',  feeds.common) .in(router)


module.exports = router