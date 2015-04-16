Q     = require 'q'
path  = require 'path'


NeutrinoClient = require path.join(__dirname, '../lib/neutrino-client')

Id             = require path.join(__dirname, '../utils/id')
Convert        = require path.join(__dirname, '../utils/convert')
Flatten        = require path.join(__dirname, '../utils/flatten')



module.exports.getBucketStores = (uuid, fields) ->
  NeutrinoClient.get (client) ->
    client.q.getBucketStores Id.forUser(uuid), Convert.toBucketStoreFields(fields)
      .then (stores) -> Flatten.bucketStores stores


module.exports.cudBucket = (uuid, cud) ->
  NeutrinoClient.get (client) ->
    client.q.cudBucket Id.forUser(uuid), Convert.toCUDBucket(cud)
