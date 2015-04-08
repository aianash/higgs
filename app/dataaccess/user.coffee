Q    = require 'q'
_    = require 'lodash'
path = require 'path'

NeutrinoClient  = require path.join(__dirname, '../lib/neutrino-client')

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')

Convert         = require path.join(__dirname, '../utils/convert')


# Instance of this User model
# is accessible after authentication
# as req.user
class User
  constructor: (info) ->
    @uuid = info.id
    @_meta = info


  detail: ->
    unless @uuid then return Q.reject(new Error('this user object has empty id field'))

    NeutrinoClient.get (client) =>
      client.q.getUserDetail Id.forUser(@uuid)


  getFriendsForInvite: (filter) ->
    filter = Convert.toFriendListFilter filter
    userId = Id.forUser @uuid

    NeutrinoClient.get (client) ->
      client.q.getFriendsForInvite userId, filter



################ Exposed methods ##################


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


  NeutrinoClient.get (client) ->
    client.q.createOrUpdateUser(req).then (userInfo) ->
      user = new User(
        id: userInfo.userId.uuid
        username: userInfo.username.handle
      )

      Q.all [user, userInfo.isNew]




exports.for = (userInfo) -> Q(new User(userInfo))