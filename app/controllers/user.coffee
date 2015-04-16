path    = require 'path'
winston = require 'winston'

logger = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.Feed = require path.join(__dirname, '../dataaccess/feed')
Flatten = require path.join(__dirname, '../utils/flatten')


###
Get user complete info

@params  {uuid}  req.user.uuid      user's unique id
@returns {Array.<Object>} info      User's info
                                    {
                                      uuid         : <Number>
                                      name         : {full: <String>, last: <String>, handle: <String>}
                                      locale       : <String>
                                      gender       : <String>
                                      facebookInfo : {fbuid: <Number>, fbToken: <String>}
                                      email        : <String>
                                      timezone     : <String>
                                      avatar       : {small: <String>, medium: <String>, large: <String>}
                                      isNew        : <Boolean>
                                    }
###
me = (req, res) ->
  req.user.getUserInfo()
    .then (info) ->
      info = Flatten.userInfo info
      info.uuid = req.user.uuid
      res.send info
    .catch (err) ->
      logger.log 'error', 'Error getting friends for user', err.message, winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



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


exports.me      = me
exports.friends = friends