path    = require 'path'
winston = require 'winston'

logger = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.Feed = require path.join(__dirname, '../dataaccess/feed')
Flatten = require path.join(__dirname, '../utils/flatten')


###
Get friends of user

@params {uuid}    req.user.uuid       user's unique id
@params {fields}  req.body            Friends Filter
                                      {
                                        location: {
                                          gpsLoc  : {lat: <Double>, lng: <Double>}
                                          title   : <String>
                                          short   : <String>
                                          full    : <String>
                                          pincode : <String>
                                          country : <String>
                                          city    : <String>
                                        }
                                      }

@returns {Array.<Object>}  friends    List of friends
                                      [
                                        {
                                          fruid: <Number>
                                          name       : {full: <String>, last: <String>, handle: <String>}
                                          avatar     : {small: <String>, medium: <String>, large: <String>}
                                        }
                                      ]
###
friends = (req, res) ->
  req.user.getFriendsForInvite req.body
    .then (friends) -> res.send Flatten.friends friends
    .catch (err) ->
      logger.log 'error', 'Error getting friends for user', err.message, winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()


exports.friends = friends