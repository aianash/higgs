Q = require 'q'

exports.verify = (clientInfo) ->
  Q( clientInfo.clientId is '918273645' and
     clientInfo.clientSecret is 'boseeinsteincondensate' )