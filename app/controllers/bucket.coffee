Q               = require 'q'
path            = require 'path'
winston         = require 'winston'

logger          = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.Bucket       = require path.join(__dirname, '../dataaccess/bucket')

###
Get Bucket's Stores and items

@params {uuid}    req.user.uuid       user's unique id
@params {fields}  req.query.fields    comma separated shopplan fields
                                      possible values include
                                      NAME, ADDRESS, ITEM_TYPES, CATALOGUE_ITEMS, CATALOGUE_ITEMS_IDS

@returns {Array.<Object>} stores      List of Bucket stores with details
                                      [
                                        {
                                          stuid  : <Number>
                                          name   : {
                                            full   : <String>
                                            handle : <String>
                                          }
                                          address: {
                                            gpsLoc  : {lat: <Double>, lng: <Double>}
                                            title   : <String>
                                            short   : <String>
                                            full    : <String>
                                            pincode : <String>
                                            country : <String>
                                            city    : <String>
                                          }
                                          itemTypes: [ <String> ]
                                          catalogueItems: [
                                            {
                                              stuid : <Number>
                                              cuid  : <Number>
                                              detail: <Catalogue detail Object>
                                            }, ...
                                          ]
                                        }, ...
                                      ]
###
stores = (req, res) ->
  fields = _.words req.query.fields || ''

  da.Bucket.list req.user.uuid, fields
    .then (stores) -> res.send stores
    .catch (err) ->
      logger.log 'error', 'Error getting bucket stores for user', err.message, winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()




###
Create/Update/Delete Bucket Store/Items

@params {uuid}    req.user.uuid   user's unique id
@params {Object}  req.body        Create/Update/Delete details for bucket
                                  [cud]
                                  {
                                    creates: {
                                      stores: [
                                        {
                                          stuid: <Number>
                                          itemTypes: [ <String> ]
                                          catalogueItems: [
                                            {
                                              stuid: <Number>
                                              cuid : <Number>
                                            }, ...
                                          ]
                                        }
                                      ]
                                    }
                                  }

@return {Boolean} success         True if successfully updated otherwise false
###
cud = (req, res) ->
  da.Bucket.cud req.user.uuid, req.body
    .then (success) -> res.send success
    .catch (err) ->
      logger.log 'error', 'Error performing cud on plan', err.message. winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



# Handlers for request
exports.stores = stores
exports.cud    = cud