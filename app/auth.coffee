util     = require 'util'
passport = require 'passport'

BearerStrategy  = require('passport-http-bearer').Strategy
HiggsFBStrategy = require __dirname + '/middlewares/higgs-fb-strategy'


### [NOTE] Higgs authentication are session less ###


# HiggsFB strategy is used to verify the client
# before creating an higgs access token
passport.use new HiggsFBStrategy()



# This strategy is used to autenticate api request
# based on higgs accessToken
passport.use new BearerStrategy (accessToken, done) ->
  # AccessTokens.find(accessToken)

  console.log "authenticating using bearer startegy"
  console.log accessToken

  user =
    username: 'ishan'
    id: 'w9898jkasjdfkasdf'

  info = scope: '*'

  done(null, user, info)