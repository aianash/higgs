bodyParser      = require 'body-parser'
Q               = require 'q'
passport        = require 'passport'
express         = require 'express'
winston         = require 'winston'

controller_path = __dirname + '/../controllers'

settings        = require __dirname + '/../settings'
logger          = require __dirname + '/../utils/logger'

router = express.Router()

plans = require controller_path + '/shopplan'


router.get '/shopplan/get', plans.getUserPlans
router.get '/shopplan/:planId/detail', plans.getShopPlan



module.exports = router