Q               = require 'q'
path            = require 'path'
winston         = require 'winston'


logger          = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.Feed         = require path.join(__dirname, '../dataaccess/feed')


friends = (req, res) ->
  req.user.getFriendsForInvite req.body
    .then (friends) ->
      res.send friends
    .catch (err) ->
      logger.log 'error', 'Error getting friends for user', err.message, winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()


exports.friends = friends