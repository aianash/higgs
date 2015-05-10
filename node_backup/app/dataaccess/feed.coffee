path  = require 'path'

NeutrinoClient = require path.join(__dirname, '../lib/neutrino-client')

Id             = require path.join(__dirname, '../utils/id')
Convert        = require path.join(__dirname, '../utils/convert')

module.exports.getCommonFeed = (filter) ->
  NeutrinoClient.get (client) ->
    client.q.getCommonFeed Convert.toFeedFilter(filter)


module.exports.getUserFeed = (uuid, filter) ->
  NeutrinoClient.get (client) ->
    client.q.getUserFeed Id.forUser(uuid), Convert.toFeedFilter(filter)