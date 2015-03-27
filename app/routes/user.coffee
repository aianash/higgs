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

user = require controller_path + '/user'

jsonParser = bodyParser.json()


Piggyback.register('GET', '/me/friends', user.friends).in(router)


module.exports = router