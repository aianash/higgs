Q = require 'q'

class User
  constructor: (info) ->
    @id = info.id

    @_meta = info


exports.createOrUpdate = (userInfo) ->
  # 1. check if user exists for a given fbuid
  # 2. if not create a new userId
  # 3. update the user info

  # dummy
  userInfo.id = userInfo.fbuid
  userInfo.username = 'kumarishan'

  Q.all([new User(userInfo), true])



exports.for = (userInfo) ->
  Q(new User(userInfo))