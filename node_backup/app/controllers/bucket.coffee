path    = require 'path'
winston = require 'winston'

logger  = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.Bucket = require path.join(__dirname, '../dataaccess/bucket')
Flatten   = require path.join(__dirname, '../utils/flatten')

###
Get Bucket's Stores and items

@params {uuid}    req.user.uuid       user's unique id
@params {fields}  req.query.fields    comma separated bucket store fields
                                      possible values include
                                      Name, Address, ItemTypes, CatalogueItems, CatalogueItemIds

@returns {Array.<Object>} stores      List of Bucket stores with details
                                      [
                                        {
                                          stuid: <Number>
                                          storeType: <String>
                                          info: {
                                            name: {
                                              full  : <String>
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
                                          catalogueItems: [
                                            {
                                              stuid : <Number>
                                              cuid  : <Number>
                                              detail: <Object>
                                            }, ...
                                          ]
                                        }, ...
                                      ]
###
stores = (req, res) ->
  fields = _.words(req.query.fields || '')

  da.Bucket.list req.user.uuid, fields
    .then (stores) -> res.send Flatten.bucketStores stores
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
                                    adds: {
                                      itemIds: [
                                        {
                                          stuid: <Number>
                                          cuid : <Number>
                                        }, ...
                                      ]
                                    }
                                    // removals and updates not supported yet
                                  }

@return {Boolean} success         True if successfully updated otherwise false
###
cud = (req, res) ->
  da.Bucket.cudBucket req.user.uuid, req.body
    .then (success) -> res.send success
    .catch (err) ->
      logger.log 'error', 'Error performing cud on bucket', err.message. winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



# Handlers for request
exports.stores = stores
exports.cud    = cud