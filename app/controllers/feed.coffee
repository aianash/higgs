Q               = require 'q'
path            = require 'path'
winston         = require 'winston'


logger          = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.Feed         = require path.join(__dirname, '../dataaccess/feed')


common = (req, res) ->
  da.Feed.getCommonFeed req.body
    .then (feed) -> res.send feed
    .catch (err) ->
      logger.log 'error', 'Error getting common feed', err.message, winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



user = (req, res) ->
  da.Feed.getUserFeed req.user.id, req.body
    .then (feed) -> res.send feed
    .catch (err) ->
      logger.log 'error', 'Error getting common feed', err.message, winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



exports.common = common
exports.user   = user