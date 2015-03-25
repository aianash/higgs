request = require 'request'

loginData =
  facebookToken: '234sdsaf'
  appSecret: 'asasdf'
  fbuid: 'kumar.ishan4'
  username: 'ishan'
  grant_type: 'token'

postReq =
  url: 'http://localhost:8080/oauth/token'
  body: loginData
  json: true

request.post postReq, (err, res, body) ->
  console.log "\n\n\nBODY"
  console.log body

request.post postReq, (err, res, body) ->
  console.log "\n\n\nBODY"
  console.log body

# request.get 'http://'