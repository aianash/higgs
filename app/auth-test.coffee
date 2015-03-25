request = require 'request'

loginData =
  facebook_token: 'CAACEdEose0cBAPh6RIIwnv5BS4YGa2wMIYIJZCJVvKwzr2MVYoZCyKxkfDRVvuMVZB1KKP0ZAwCjuMZAxhdZCpyS5ZAFFqvSEUjks5AfqD7ZCBwGnCpA3acYENlffW5ysJitR153PNxqiPqpzXYFPh32wq6WZABRZCcMvZCa3APqTnZAJ0aq8E3t3xxsJi5hwWWWSrnozCvkSvLHUBlP5BCepWoRZAqLGU1EQUtPVHEjZCjKgzZCwZDZD'
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