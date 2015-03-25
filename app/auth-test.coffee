request = require 'request'

loginData =
  facebookToken: '234sdsaf'
  appSecret: 'asasdf'
  fbuid: 'kumar.ishan4'
  username: 'ishan'
  grant_type: 'token'


postReq =
  url: 'http://higgs.com:8080/oauth/token'
  body: loginData
  json: true

# Request an Access Token
request.post postReq, (err, res, body) ->
  console.log "\n\n\nBODY"
  console.log body

  # Use the access Token to get plans (API)
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

  request.get 'http://higgs.com:8080/apiv1/plans/get',
    auth:
      bearer: body.access_token
    ,(err, res, body) ->
      console.log body