path = require 'path'

NeutrinoClient  = require path.join(__dirname, '../lib/neutrino-client')

Id              = require path.join(__dirname, '../utils/id')
Convert         = require path.join(__dirname, '../utils/convert')

module.exports.search = (uuid, sruid, request) ->
  NeutrinoClient.get (client) ->
    client.q.search Convert.toCatalogueSearchRequest uuid, sruid, request