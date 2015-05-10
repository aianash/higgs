Q           = require 'q'
oauth2orize = require 'oauth2orize'
passport    = require 'passport'
login       = require 'connect-ensure-login'
winston     = require 'winston'
path        = require 'path'

da = {} # namespacing under dataaccess
da.User        = require path.join(__dirname, '/dataaccess/user')
da.AccessToken = require path.join(__dirname, '/dataaccess/access-token')

logger      = require path.join(__dirname, '/utils/logger')

# Create OAuth 2.0 server
server = oauth2orize.createServer()


# Create a new AccessToken for the authenticated user
#
# It assumes user has been authenticated and its details
# are in userInfo which will be created/updated in database
#
# [NOTE] no scope info is received yet
server.exchange 'token', oauth2orize.exchange.clientCredentials (userInfo, scope, done) ->

  logger.log('info', 'Creating a new token for user info', userInfo)

  ####
  createAccessToken = (user) ->
    if not user then return Q.reject(new Error('null user received'))

    da.AccessToken.newToken user
      .then (token) -> done null, token


  sendVerificationMail = (user, isNew) ->
    # implement this later
    Q(user)


  handleError = (err) ->
    logger.log('error', 'Error creating user object', err.message, winston.exception.getTrace(err))
    done err, null

  ####

  return da.User.createOrUpdate userInfo
      .spread sendVerificationMail
      .then createAccessToken
      .catch handleError
      .done()



# First client should authenticate using HiggsFB strategy
# then generate the token
exports.token = [
  passport.authenticate ['higgsFB'], {session: false}
  server.token()
  server.errorHandler()
]