request = require 'request'

loginData =
  facebook_token: 'CAACEdEose0cBAPYZCYey4LihOIcrLAnTHCfpL0enVoLal66FZAOnLZBSBbkgAZAkF4TOdeCxaj1XpQUoL80ZCebl19t3BbOcc1zh6yp8x7xYv0gw8SHrFUydPWTtKvJ6gZBQ3AGc1FmG8PXX8KQSUTRZCcCs6kkZCS8kQPu732n25jFFbAepqmXozTyaicgtppnKQrDR2v8LBYTReS5Yfw8eiUrenxA6ginmH7ZACOPJ2ogZDZD'
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
  request.get 'http://higgs.com:8080/apiv1/shopplan/1888928277739/detail',
    auth:
      bearer: body.access_token
    ,(err, res, body) ->
      console.log body

  request.get 'http://higgs.com:8080/apiv1/shopplan/get',
    auth:
      bearer: body.access_token
    ,(err, res, body) ->
      console.log body

  # request.get 'http://higgs.com:8080/apiv1/shopplan/get',
  #   auth:
  #     bearer: body.access_token
  #   ,(err, res, body) ->
  #     console.log body