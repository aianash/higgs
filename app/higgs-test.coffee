request = require 'request'
FB = require 'fb'

FB.options
  appId : '800587516688651'
  appSecret: 'b59466a3500f21168131758c3dba4ce1'


loginData =
  facebook_token: 'CAACEdEose0cBAMKbQ0Kv7GYHBjrOirLP5TtEEqQMFoE8lbkb7cJxZBidfcZCtfXEri8wxMIFgO7JLgFsSAZBFY4Jyb0pvxo0Kvk9aDaB3aPKZAykkgQfpYwiZC2AnydUimZB5H9nfPl9zQ9tY1n1RgoeH8S9H8HZCQkLZAzq1irYCGHdD5FdLf97YYkIGCjkygcsDyI3jy58EL0X9kkJsLFXGwKFG5cNpBxfvexERiBumgZDZD'
  client_secret: 'boseeinsteincondensate'
  client_id: '918273645'
  fbuid: '10203676044758492'
  grant_type: 'token'


endpoint = 'http://higgs.com:8080/'
postReq =
  url: endpoint + 'oauth/token'
  body: loginData
  json: true

prefix = endpoint + 'apiv1/'
shopplanId = 1888928277739





# Request an Access Token
request.post postReq, (err, res, body) ->
  if err then return console.log(err)


  console.log "\n\n\Authentication response"
  console.log body

  accessToken = body.access_token

  wrongAccessToken = accessToken
  wrongAccessToken = wrongAccessToken.substr(0, wrongAccessToken.length - 1) + 'B'

  detailReq =
    url: "#{prefix}shopplan/#{shopplanId}/detail"
    body: {_piggybacks: []}
    json: true
    auth:
      bearer: accessToken

  # Use the access Token to get plans (API)
  request.get detailReq, (err, res, body) ->
    console.log "\n\nDetail response"
    console.log body

  request.get prefix + 'shopplan/all',
    auth:
      bearer: accessToken
    ,(err, res, body) ->
      console.log "\n\nAll response"
      console.log body

  updateData =
    stores: {
      1019928288899282: {
        collections: [92888289993829]
      }
    }


  updateReq =
    url: "#{prefix}shopplan/#{shopplanId}/update"
    body: updateData
    json: true
    auth:
      bearer: accessToken

  request.post updateReq, (err, res, body) ->
    console.log "\n\nUpdate response"
    console.log body
