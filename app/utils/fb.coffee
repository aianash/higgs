crypto  = require('crypto')

module.exports.createAppSecretProof = (token, appSecret) ->
  hmac = crypto.createHmac('sha256', appSecret)
  hmac.update(token)
  return hmac.digest('hex')