Q     = require 'q'
_     = require 'lodash'
path  = require 'path'

NeutrinoClient  = require path.join(__dirname, '../lib/neutrino-client')

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
shopplan_ttypes = require path.join(__dirname, '../lib/shopplan_types')

Id        = require path.join(__dirname, '../utils/id')
FeedUtil  = require path.join(__dirname, '../utils/feed')

toFeedFilter = (filter) ->


module.exports.getCommonFeed = (filter) ->
  NeutrinoClient.get (client) ->
    client.q.getCommonFeed FeedUtil.createFilter filter
      .then (feed) -> FeedUtil.mergeTransform feed



module.exports.getUserFeed = (uuid, filter) ->
  NeutrinoClient.get (client) ->
    client.q.getUserFeed Id.forUser(uuid), FeedUtil.createFilter(filter)
      .then (feed) -> FeedUtil.mergeTransform feed
