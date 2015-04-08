Q               = require 'q'
path            = require 'path'
winston         = require 'winston'


logger          = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.Feed         = require path.join(__dirname, '../dataaccess/feed')


# Returns common feed
#
# @params {Request}  req.body contains filter
#                       {
#                         page: <page number to fetch>
#                         city: <name of the city>
#                       }
#
# @returns {Response} {
#                        data: <Array of posts>
#                        page: <page number of the feed>
#                     }
#
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



# Returns common feed
#
# @params {Request}  req.user.uuid contains the uuid for user
#                    req.body contains filter
#                       {
#                         page: <page number to fetch>
#                         city: <name of the city>
#                       }
#
# @returns {Response} {
#                        data: <Array of posts>
#                        page: <page number of the feed>
#                     }
#
user = (req, res) ->
  da.Feed.getUserFeed req.user.uuid, req.body
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