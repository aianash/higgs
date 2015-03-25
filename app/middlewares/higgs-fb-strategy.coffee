Strategy = require 'passport-strategy'

###
HiggsFBStrategy

This strategy is used to authenticate users based on the already
authorized FB access token (which they got after Facebook login)
used
###
class HiggsFBStrategy extends Strategy

  constructor: (options, verify) ->
    if typeof options == 'function'
      verify = options
      options = {}

    options = options || {}

    @_facebookTokenField = options.facebookTokenField || 'facebookToken'
    @_appSecretField     = options.appSecretField     || 'appSecret'
    @_fbuidField         = options.fbuidField         || 'fbuid'
    @_usernameField      = options.usernameField      || 'username'

    Strategy.call @

    @name = 'higgsFB'

    @_passReqToCallback = options._passReqToCallback


  # Actual authenticate method called by passport
  # i.e. when passport.authenticate ['higgsFB'] is used
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
      return @fail(
        message: options.badRequestMessage || 'Missing credentials', 400)

    console.log 'verifying facebook token'

    # Verify the passed credentials
    # 1. Verify if Facebook accessToken is valid
    # 2. Verify if identified FB user is associated with username in Higgs
    # 3. Verify app using appSecret
    #
    # Once verified return information to be stored
    # for the user.
    user =
      username: username
      id: '02092klkskk'
      fbAccessToken: '90290290290kjskjlskjlskljs'
      fbuid: 'kumar.ishan4'


    @success user, scope: '*'


module.exports = HiggsFBStrategy