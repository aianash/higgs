request = require 'request'
FB = require 'fb'

FB.options
  appId : '800587516688651'
  appSecret: 'b59466a3500f21168131758c3dba4ce1'


# [IMP] facebook_token and fbuid are to be generated using
# Graph ui explorer for Shoplane App
loginData =
  facebook_token: 'CAALYIU6LOQsBAA3jCMBSE4Jn7b84ZB575OCwzNsZAJb4DU3FCnQVlhSEYoKREqcYDSvnZAvDkILlIlWkS3yChQWomcOqH4J8X9Ml3vshIFnKD1HCCG2gZAACmtBb2XJZBThgJIqMJz1HO6jlw2ZCLjdBpiLgugdRBMLMLlzT4XnJZBLLQ1HZCkZA0SlD6b3xjXOOFZAkRHZCCah5DoYN5skqsZAlieApRSz8U31cfh4LwqPLvwZDZD'
  client_secret: 'boseeinsteincondensate'
  client_id: '918273645'
  fbuid: '10205798015366431'
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

  addData =
    stores: {
      1019928288899282: {
        collections: [92888289993829]
      }
    }


  addReq =
    url: "#{prefix}shopplan/#{shopplanId}/add"
    body: addData
    json: true
    auth:
      bearer: accessToken

  request.post addReq, (err, res, body) ->
    console.log "\n\nAdd response"
    console.log body
