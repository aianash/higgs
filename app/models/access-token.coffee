Q = require 'q'
fs = require 'fs'
path = require 'path'
jwt = require 'jsonwebtoken'

settings = require __dirname + '/../settings'

publiccert  = fs.readFileSync(path.join(__dirname, '../../', settings.get('accessToken:publiccert')))
privatecert = fs.readFileSync(path.join(__dirname, '../../', settings.get('accessToken:privatecert')))

issuer = settings.get('accessToken:issuer')

signOpts =
  algorithm: settings.get('accessToken:algorithm')
  expiresInMinutes: settings.get('accessToken:expiry')
  issuer: issuer


# Create a new token for the userId
# and update it in the database
exports.newToken = (user) ->
  Q(jwt.sign(id: user.id, privatecert, signOpts))



exports.getUserInfoFrom = (accessToken) ->
  deferred = Q.defer()

  jwt.verify(accessToken, publiccert, {issuer}, (err, decoded) ->
    if err then deferred.reject(err)
    else deferred.resolve(decoded)
  )

  deferred.promise