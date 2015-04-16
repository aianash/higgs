path      = require 'path'
express   = require 'express'

Piggyback = require path.join(__dirname, '../middlewares/piggyback')
user      = require path.join(__dirname, '../controllers/user')

router    = express.Router()

Piggyback.register('GET',  '/me/friends',    user.friends)   .in(router)

module.exports = router