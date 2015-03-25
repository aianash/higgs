util     = require 'util'
passport = require 'passport'
winston  = require 'winston'

BearerStrategy  = require('passport-http-bearer').Strategy
HiggsFBStrategy = require __dirname + '/middlewares/higgs-fb-strategy'

AccessToken = require __dirname + '/models/access-token'
User        = require __dirname + '/models/user'

logger = require __dirname + '/utils/logger'


### [NOTE] Higgs authentication are session less ###


# HiggsFB strategy is used to verify the client
# before creating an higgs access token
passport.use new HiggsFBStrategy()



# This strategy is used to autenticate api request
# based on higgs accessToken
passport.use new BearerStrategy (accessToken, done) ->

  AccessToken.getUserInfoFrom(accessToken)
    .then (userInfo) -> User.for(userInfo)
    .then (user) ->
      if not user then Q.reject(new Error('user received null'))
      done(null, user)

    .catch (err) ->
      trace = winston.exception.getTrace(trace)
      logger.log('error', 'Error resolving access token to userId', err.message, trace)
      done(err)
    .done()