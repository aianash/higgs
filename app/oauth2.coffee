oauth2orize = require 'oauth2orize'
passport    = require 'passport'
login       = require 'connect-ensure-login'


# Create OAuth 2.0 server
server = oauth2orize.createServer()


server.exchange 'token', oauth2orize.exchange.clientCredentials (client, scope, done) ->
  # Clients.findByClientId client.clientId
  # Check: localClient.clientSecret === client.clientSecret

  console.log "Client"
  console.log client

  console.log "scope"
  console.log scope

  token = Math.random().toString(16).substr(2)
  console.log("Created this token for" + token)

  # AccessTokens.save(token, user.id, client.clientID)
  done null, token


# First client should authenticate using HiggsFB strategy
# then generate the token
exports.token = [
  passport.authenticate ['higgsFB'], {session: false}
  server.token()
  server.errorHandler()
]