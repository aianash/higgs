Q = require 'q'
_ = require 'lodash'
path = require 'path'

NeutrinoClient = require path.join(__dirname, '../lib/neutrino-client')

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')


# Instance of this User model
# is accessible after authentication
# as req.user
class User
  constructor: (info) ->
    @id = info.id

    @_meta = info

  detail: ->
    unless @id then return Q.reject(new Error('this user object has empty id field'))

    NeutrinoClient.get().then (client) =>
      client.q.getUserDetail(new common_ttypes.UserId(uuid: @id))
        .finally -> NeutrinoClient.release client



exports.createOrUpdate = (userInfo) ->

  locale = common_ttypes.Locale.EN_US
  gender = common_ttypes.Gender[userInfo.gender.toUpperCase()]

  fbUserId = new common_ttypes.UserId
    uuid: userInfo.fbuid
    type: common_ttypes.UserIdType.FB

  facebookInfo = new neutrino_ttypes.FacebookInfo
    userId: fbUserId
    token : userInfo.fbToken


  names = new common_ttypes.UserName
    first: userInfo.firstName
    last : userInfo.lastName


  req = new neutrino_ttypes.UserInfo({
    names,
    locale,
    gender,
    facebookInfo,
    email: userInfo.email
    #[TO DO] timezone: userInfo.timezone
  })


  NeutrinoClient.get().then (client) ->
    client.q.createOrUpdateUser(req).then (userInfo) ->
      user = new User(
        id: userInfo.userId.uuid
        username: userInfo.username.handle
      )

      Q.all [user, userInfo.isNew]
    .finally -> NeutrinoClient.release client



exports.for = (userInfo) -> Q(new User(userInfo))