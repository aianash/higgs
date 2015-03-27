Q        = require 'q'
path     = require 'path'
Strategy = require 'passport-strategy'
FB       = require 'fb'

logger     = require path.join(__dirname, '../utils/logger')
settings   = require path.join(__dirname, '../settings')
ClientApps = require path.join(__dirname, '../dataaccess/client-apps')

FB.options
  appId    : settings.get('facebook:appId')
  appSecret: settings.get('facebook:appSecret')


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

    @_facebookTokenField = options.facebookTokenField || 'facebook_token'
    @_clientIdField      = options.clientIdField      || 'client_id'
    @_clientSecretField  = options.clientSecretField  || 'client_secret'
    @_fbuidField         = options.fbuidField         || 'fbuid'
    @_usernameField      = options.usernameField      || 'username'

    Strategy.call @

    @name = 'higgsFB'



  # Actual authenticate method called by passport
  # i.e. when passport.authenticate ['higgsFB'] is used
  authenticate: (req, options) ->
    options = options || {}

    facebookToken = req.body[@_facebookTokenField]
    fbuid         = req.body[@_fbuidField]

    # clientInfo to identify Higgs client
    clientId         = req.body[@_clientIdField]
    clientSecret     = req.body[@_clientSecretField]


    if !facebookToken || !clientId || !clientSecret || !fbuid
      return @fail(
        message: 'Missing credentials', 400)


    # Verify the passed credentials
    # 1. Verify client using appSecret
    # 2. Verify if Facebook accessToken is valid
    #
    # Once verified return information to be stored
    # for the user.
    ClientApps.verify({clientId, clientSecret})
      .then (verified) ->
        if not verified then return Q.reject(new Error('Unrecognized app'))

        parameters =
          access_token: facebookToken

        deferred = Q.defer()

        FB.api '/me', 'get', parameters, (result) ->
          if !result || result.error || not result.verified
            return deferred.reject new Error('Facebook access token not authorized ' + result.error.message)

          if result.id isnt fbuid
            return deferred.reject new Error('Invalid access token for the user with id ' + fbuid)

          # User info that will be used to create/update user account
          # with higgs
          userInfo =
            firstName:  result.first_name
            lastName:   result.last_name
            name:       result.name
            locale:     result.locale
            location:   result.location.name
            gender:     result.gender
            fbuid:      result.id
            email:      result.email
            timezone:   result.timezone


          deferred.resolve userInfo

        deferred.promise

      .then (userInfo) =>
        @success userInfo, scope: '*'
      .catch (err) =>
        logger.log('error', err.message)
        @fail(message: err.message, 400)
      .done()

module.exports = HiggsFBStrategy