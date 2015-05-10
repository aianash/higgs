path    = require 'path'
winston = require 'winston'

logger  = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.Search = require path.join(__dirname, '../dataaccess/search')
Flatten   = require path.join(__dirname, '../utils/flatten')

###
Search items

@params {Number}   req.user.uuid      user's unique id
@params {Number}   req.params.sruid   search's unique id
@params {String}   req.body.query     query text for search
@params {Number}   req.body.pageIndex page index
@params {Number}   req.body.pageSize  page size


@returns {Object}  results            Search result
                                      {
                                        sruid: <Numnber>
                                        result: [
                                          {
                                            stuid: <Number>
                                            storeType: <String>
                                            info: {
                                              name: {
                                                full: <String>
                                                handle: <String>
                                              }
                                              itemTypes: [<String>]
                                              address: {
                                                gpsLoc  : {lat: <Double>, lng: <Double>}
                                                title   : <String>
                                                short   : <String>
                                                full    : <String>
                                                pincode : <String>
                                                country : <String>
                                                city    : <String>
                                              }
                                              avatar: {
                                                small: <String>
                                                medium: <String>
                                                large: <String>
                                              }
                                              email: <String>
                                              phoneContact: <Array.<String>>
                                            }
                                            items: [
                                              {
                                                stuid: <Number>
                                                cuid: <Number>
                                                detail: <Object>
                                              }, ...
                                            ]
                                          }, ...
                                        ]
                                      }
###
search = (req, res) ->
  da.Search.search req.user.uuid, req.sruid, req.body
    .then (result) -> res.send Flatten.searchResult result
    .catch (err) ->
      logger.log 'error', 'Error getting search result'
      res.send
        error:
          message: err.message
          type: typeof err
    .done()