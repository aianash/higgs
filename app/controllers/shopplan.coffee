path    = require 'path'
winston = require 'winston'


logger  = require path.join(__dirname, '../utils/logger')

da = {} # scoping under dataaccess (da)
da.ShopPlan = require path.join(__dirname, '../dataaccess/shopplan')
Flatten     = require path.join(__dirname, '../utils/flatten')


###
List User's own or invited shop plans

@params {uuid}    req.user.uuid       user's unique id
@params {fields}  req.query.fields    comma separated shopplan fields
                                      possible values include
                                      TITLE, STORES, CATALOGUE_ITEMS, DESTINATIONS, INVITES

@returns {Array.<Object>} plans List of shop plans for the user
                                [
                                  {
                                    createdBy: <Number>
                                    suid     : <Number>
                                    title    : <String>
                                    stores: [
                                      {
                                        stuid    : <Number>
                                        suid     : <Number>
                                        createdBy: <Number>
                                        dtuid    : <Number>
                                        name: {
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
                                            stuid  : <Number>
                                            cuid   : <Number>
                                            detail: <Catalogue detail Object>
                                          }, ...
                                        ]
                                      }, ...
                                    ]
                                    destinations: [
                                      {
                                        suid     : <Number>
                                        createdBy: <number
                                        dtuid    : <Number>
                                        address: {
                                          gpsLoc  : {lat: <Double>, lng: <Double>}
                                          title   : <String>
                                          short   : <String>
                                          full    : <String>
                                          pincode : <String>
                                          country : <String>
                                          city    : <String>
                                        }
                                        numShops: <Number>
                                      }, ...
                                    ]
                                    invites: [
                                      {
                                        fruid      : <Number>
                                        createdBy  : <Number>
                                        suid       : <Number>
                                        name       : {full: <String>, last: <String>, handle: <String>}
                                        avatar     : {small: <String>, medium: <String>, large: <String>}
                                      }, ...
                                    ]
                                    isInvitations: <Boolean>
                                  }, ....
                                ]
###
list = (req, res) ->
  method = req.query.filter == 'invited' ? 'ownPlans' : 'invitedPlans'
  fields = _.words req.query.fields || ""

  da.ShopPlan[method] req.user.uuid, fields
    .then (plans) -> res.send Flatten.shopPlans plans
    .catch (err) ->
      logger.log('error', 'Error getting all plans of user', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()




###
Create a new shop plan

@params {uuid}    req.user.uuid       user's unique id
@params {Object}  req.body            Shop plan details for creating it
                                      {
                                        creates: {
                                          title: <String>
                                          destinations: [
                                            {
                                              dtuid    : <Number>
                                              address: {
                                                gpsLoc: {lat: <Double>, lng: <Double>}
                                              }
                                              numShops: <Number|-1>
                                            }, ...
                                          ]
                                          invites: [
                                            {
                                              fruid      : <Number>
                                              name       : {full: <String>, last: <String>, handle: <String>}
                                              avatar     : {small: <String>, medium: <String>, large: <String>}
                                            }, ...
                                          ]
                                          stores: [
                                            {
                                              stuid    : <Number>
                                              dtuid    : <Number|-1>
                                              itemTypes: [ <String> ]
                                              catalogueItemIds: [ <Number> ]
                                            }, ...
                                          ]
                                        }
                                      }

@returns {Object}   shopplanId        {createdBy: <Number>, suid: <Number>}
###
create = (req, res) ->
  da.ShopPlan.new req.user.uuid, req.body
    .then (shopplanId) -> res.send Flatten.shopPlanId shopplanId
    .catch (err) ->
      logger.log('error', 'Error creating new plan for user', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()




###
Get shop plan of user

@params {uuid}    req.user.uuid       user's unique id
@params {suid}    req.params.suid     shop plan's unique id
@params {fields}  req.query.fields    comma separated shopplan fields
                                      possible values include
                                      TITLE, STORES, CATALOGUE_ITEMS, DESTINATIONS, INVITES

@returns {Object} plan                shop plan detail for the user
                                      {
                                        createdBy: <Number>
                                        suid     : <Number>
                                        title    : <String>
                                        stores: [
                                          {
                                            stuid    : <Number>
                                            suid     : <Number>
                                            createdBy: <Number>
                                            dtuid    : <Number>
                                            name: {
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
                                                stuid  : <Number>
                                                cuid   : <Number>
                                                detail: <Catalogue detail Object>
                                              }, ...
                                            ]
                                          }, ...
                                        ]
                                        destinations: [
                                          {
                                            suid     : <Number>
                                            createdBy: <number
                                            dtuid    : <Number>
                                            address: {
                                              gpsLoc  : {lat: <Double>, lng: <Double>}
                                              title   : <String>
                                              short   : <String>
                                              full    : <String>
                                              pincode : <String>
                                              country : <String>
                                              city    : <String>
                                            }
                                            numShops: <Number>
                                          }, ...
                                        ]
                                        invites: [
                                          {
                                            fruid      : <Number>
                                            createdBy  : <Number>
                                            suid       : <Number>
                                            name       : {full: <String>, last: <String>, handle: <String>}
                                            avatar     : {small: <String>, medium: <String>, large: <String>}
                                          }, ...
                                        ]
                                        isInvitations: <Boolean>
                                      }
###
get = (req, res) ->
  fields = _.words req.query.fields || ""

  da.ShopPlan.get req.user.uuid, req.params.suid, fields
    .then (plan) -> res.send Flatten.shopPlan plan
    .catch (err) ->
      logger.log('error', 'Error getting plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()




###
Create/Update/Delete Shop plan's components

@params {uuid}    req.user.uuid       user's unique id
@params {suid}    req.params.suid     shop plan's unique id
@params {Object}  req.body            Create/Update/Delete details for shop plan
                                      {
                                        adds: {
                                          destinations: [
                                            {
                                              dtuid    : <Number>
                                              address: {
                                                gpsLoc: {lat: <Double>, lng: <Double>}
                                              }
                                              numShops: <Number|-1>
                                            }, ...
                                          ]
                                          invites: [
                                            {
                                              fruid      : <Number>
                                              name       : {full: <String>, last: <String>, handle: <String>}
                                              avatar     : {small: <String>, medium: <String>, large: <String>}
                                            }, ...
                                          ]
                                          stores: [
                                            {
                                              stuid    : <Number>
                                              dtuid    : <Number|-1>
                                              itemTypes: [ <String> ]
                                              catalogueItemIds: [ <Number> ]
                                            }, ...
                                          ]
                                        }
                                        updates: {
                                          title: <String>
                                          destinations: [
                                            {
                                              dtuid    : <Number>
                                              address: {
                                                gpsLoc: {lat: <Double>, lng: <Double>}
                                              }
                                              numShops: <Number|-1>
                                            }, ...
                                          ]
                                        }
                                        removals: {
                                          destinations: [ <Number> ]
                                          invites     : [ <Number> ]
                                          stores      : [ <Number> ]
                                        }
                                      }

@returns {boolean} success            True if update is successful otherwise false
###
cud = (req, res) ->
  da.ShopPlan.cud req.user.uuid, req.params.suid, req.body
    .then (success) -> res.send success
    .catch (err) ->
      logger.log('error', 'Error performing cud on plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()




###
@params   {uuid}      req.user.uuid       user's unique id
@params   {suid}      req.params.suid     shop plan's unique id
@returns  {boolean}   success             True if update successful otherwise false
###
end = (req, res) ->
  da.ShopPlan.end req.user.uuid, req.params.suid
    .then (success) -> res.send success
    .catch (err) ->
      logger.log('error', 'Couldnt end the plan', err.message, winston.exception.getTrace(err))
      res.send
        error:
          message: err.message
          type: typeof err
    .done()


###
Get Shop Plan's stores

@params   {uuid}      req.user.uuid       user's unique id
@params   {suid}      req.params.suid     shop plan's unique id
@params {fields}      req.query.fields    comma separated shopplan fields
                                          possible values include
                                          TITLE, STORES, CATALOGUE_ITEMS, DESTINATIONS, INVITES

@returns {Array.<Object>} stores          List of Shop Plan stores
                                          [
                                            {
                                              stuid    : <Number>
                                              suid     : <Number>
                                              createdBy: <Number>
                                              dtuid    : <Number>
                                              name: {
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
                                                  stuid  : <Number>
                                                  cuid   : <Number>
                                                  detail: <Catalogue detail Object>
                                                }, ...
                                              ]
                                            }, ...
                                          ]

###
stores = (req, res) ->
  fields = _.words req.query.fields || ""

  da.ShopPlan.stores req.user.uuid, req.params.suid, fields
    .then (stores) -> res.send Flatten.shopPlanStores stores
    .catch (err) ->
      logger.log 'error', 'Couldnt get stores for the plan', err.message, winston.exception.getTrace(err)
      res.send
        error:
          message: err.message
          type: typeof err
    .done()



# Handlers for request
exports.list      = list
exports.create    = create
exports.get       = get
exports.cud       = cud
exports.end       = end
exports.stores    = stores