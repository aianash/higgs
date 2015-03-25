request = require 'request'

loginData =
  facebook_token: 'CAACEdEose0cBADpJYlWwAmOLxSr1pIFh5694PuRz83ZBDfZBOFCm336PMK07PZCkWZBJSqvE5IvL4jbGG4nvdLZAbLdoNrc1AtHtijXViuaT6vQ4ven1PI6BJCPbrRaOblL54AI9MOV61uPgJR4WZCQSv9YGYUgs7rIMSDcVZBJvJDivuevCUhizW5ZAgcZCKVAE9415N2WPTk6kvJeBqURhqa4CAuhFLVZCjaimJRqTrKJAZDZD'
  client_secret: 'boseeinsteincondensate'
  client_id: '918273645'
  fbuid: '10203676044758492'
  grant_type: 'token'


postReq =
  url: 'http://higgs.com:8080/oauth/token'
  body: loginData
  json: true

# Request an Access Token
request.post postReq, (err, res, body) ->
  console.log "\n\n\nBODY"
  console.log body

  wrongAccessToken = body.access_token
  wrongAccessToken = wrongAccessToken.substr(0, wrongAccessToken.length - 1) + 'B'

  # Use the access Token to get plans (API)
  request.get 'http://higgs.com:8080/apiv1/plans/get',
    auth:
      bearer: wrongAccessToken
    ,(err, res, body) ->
      console.log body

  request.get 'http://higgs.com:8080/apiv1/plans/get',
    auth:
      bearer: body.access_token
    ,(err, res, body) ->
      console.log body

  request.get 'http://higgs.com:8080/apiv1/plans/get',
    auth:
      bearer: body.access_token
    ,(err, res, body) ->
      console.log body