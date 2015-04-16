Q               = require 'q'
path            = require 'path'
winston         = require 'winston'


logger          = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.Feed         = require path.join(__dirname, '../dataaccess/feed')


###
Get common feed

@params   {Object}  req.body    Feed filter object
                                {
                                  city: <String>
                                  page: <Number>
                                }

@returns {Array.<Object>} feed  List of posts (currently two types of posts)
                                [
                                  {
                                    ptuid : <Number>
                                    idx   : <Number>
                                    type  : 'offer'
                                    from  :
                                      stuid  : <Number>
                                      name   : {full: <String>, handle: <String>}
                                      address: {
                                        gpsLoc  : {lat: <Double>, lng: <Double>}
                                        title   : <String>
                                        short   : <String>
                                        full    : <String>
                                        pincode : <String>
                                        country : <String>
                                        city    : <String>
                                      }
                                    title   : <String>
                                    subtitle: <String>
                                  },
                                  {
                                    ptuid : <Number>
                                    idx   : <Number>
                                    type  : 'posterAd'
                                    paduid: <Number>
                                    image : <String>
                                  }
                                ]
###
common = (req, res) ->
  da.Feed.getCommonFeed req.body
    .then (feed) -> res.send Flatten.feed feed
    .catch (err) ->
      logger.log 'error', 'Error getting common feed', err.message, winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



###
Get user feed

@params   {Number}  req.user.uuid   User's unique id
@params   {Object}  req.body        Feed filter object
                                    {
                                      city: <String>
                                      page: <Number>
                                    }

@returns {Array.<Object>} feed      List of posts (currently two types of posts)
                                    [
                                      {
                                        ptuid : <Number>
                                        idx   : <Number>
                                        type  : 'offer'
                                        from  :
                                          stuid  : <Number>
                                          name   : {full: <String>, handle: <String>}
                                          address: {
                                            gpsLoc  : {lat: <Double>, lng: <Double>}
                                            title   : <String>
                                            short   : <String>
                                            full    : <String>
                                            pincode : <String>
                                            country : <String>
                                            city    : <String>
                                          }
                                        title   : <String>
                                        subtitle: <String>
                                      },
                                      {
                                        ptuid : <Number>
                                        idx   : <Number>
                                        type  : 'posterAd'
                                        paduid: <Number>
                                        image : <String>
                                      }
                                    ]
###
user = (req, res) ->
  da.Feed.getUserFeed req.user.uuid, req.body
    .then (feed) -> res.send Flatten.feed feed
    .catch (err) ->
      logger.log 'error', 'Error getting common feed', err.message, winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



exports.common = common
exports.user   = user