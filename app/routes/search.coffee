path            = require 'path'
express         = require 'express'

Piggyback       = require path.join(__dirname, '../middlewares/piggyback')

router = express.Router()

search = require path.join(__dirname, '../controllers/search')

jsonParser = bodyParser.json()

Piggyback.register('GET', '/search/:sruid', search.search).in(router)

module.exports = router