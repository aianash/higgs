util     = require 'util'
passport = require 'passport'
Strategy = require 'passport-strategy'
BearerStrategy = require('passport-http-bearer').Strategy
ClientPasswordStrategy = require('passport-oauth2-client-password').Strategy

###
HiggsFBStrategy

This strategy is used to authenticate users based on the already
authorized FB access token, they got after signin Facebook
used
###
class HiggsFBStrategy extends Strategy

  constructor: (options, verify) ->
    if typeof options == 'function'
      verify = options
      options = {}

    options = options || {}

    @_facebookTokenField = options.facebookTokenField || 'facebookToken'
    @_appSecretField     = options.appSecretField || 'appSecret'
    @_fbuidField         = options.fbuidField || 'fbuid'
    @_usernameField      = options.usernameField || 'username'

    Strategy.call @

    @name = 'higgsFB'

    # @_callback = callback
    @_passReqToCallback = options._passReqToCallback

  authenticate: (req, options) ->
    options = options || {}

    # console.log req
    # console.log 'inside authenticate'
    # console.log req.body

    facebookToken = req.body[@_facebookTokenField]
    appSecret     = req.body[@_appSecretField]
    username      = req.body[@_usernameField]
    fbuid         = req.body[@_fbuidField]

    if !facebookToken || !appSecret || !fbuid
      return @fail(message: options.badRequestMessage || 'Missing credentials', 400)

    # console.log 'verifying facebook token'
    # Verify the passed credentials
    # 1. Verify if Facebook accessToken is valid
    # 2. Verify if identified FB user is associated with username in Higgs
    # 3. Verify app using appSecret
    user =
      username: username
      id: '02092klkskk'
      fbAccessToken: '90290290290kjskjlskjlskljs'
      fbuid: 'kumar.ishan4'

    # info =

    @success user, null


passport.use new HiggsFBStrategy() # using default field names in req body


passport.serializeUser (user, done) ->
  done null, user.username

passport.deserializeUser (username, done) ->
  done null, username: username


# passport.use new ClientPasswordStrategy (clientId, clientSecret, done) ->
#   # Clients.findByClientId clientId
#   #  .then (client) ->
#   #     if client.clientSecret === clientSecret
#   client = # Sample client
#     id: '1'
#     clientId: clientId
#     clientSecret: clientSecret
#     name: 'boson'
#   done null, client


###
BearerStrategy

This strategy is used to autenticate api request based on accessToken
###
passport.use new BearerStrategy (accessToken, done) ->
  # AccessTokens.find(accessToken)
  console.log accessToken

  user = username: 'ishan'
  info = scope: '*'

  done(null, user, info)
