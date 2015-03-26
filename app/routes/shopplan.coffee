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

plans = require controller_path + '/shopplan'

jsonParser = bodyParser.json()

Piggyback.register('GET',  '/shopplan/all',             plans.getUserPlans)   .in(router)
Piggyback.register('GET',  '/shopplan/:planId/detail',  plans.getShopPlan)    .in(router)
Piggyback.register('POST', '/shopplan/:planId/update',  plans.updateShopPlan) .in(router)


module.exports = router