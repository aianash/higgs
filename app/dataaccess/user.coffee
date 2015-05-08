Q    = require 'q'
_    = require 'lodash'
path = require 'path'

NeutrinoClient  = require path.join(__dirname, '../lib/neutrino-client')
Convert         = require path.join(__dirname, '../utils/convert')

Id = require path.join(__dirname, '../utils/id')


###
User Model, whose instance is accessible
after authentication
###
class User

  constructor: (info) ->
    @uuid  = info.uuid
    if !_.isNumber @uuid then throw new TypeError('Error creating user object, no uuid provided')

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



  getUserInfo: ->
    userId = Id.forUser @uuid

    NeutrinoClient.get (client) ->
      client.q.getUserDetail userId



################ Exposed methods ##################


exports.createOrUpdate = (userInfo) ->
  NeutrinoClient.get (client) ->
    client.q.createUser Convert.toUserInfo userInfo
      .then (userId) -> new User userId



exports.for = (userInfo) -> Q(new User(userInfo))