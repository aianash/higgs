# Higgs

Higgs service provide REST Apis for Boson App (and possibly others in future).
REST APIs are implemented in separate modules like bucket, shopplan, search, user, etc.
This service currently depends on three backend services -
- Neutrino - For shop plan, bucket, user and feed
- Creed - For search results
- Cassie - For resolving search results' items

Each module follows a generic pattern for carrying out operations of its apis
- routes are mapped to action in controllers
- controllers reads json in request body as object using json combinators defined in model
  in respective modules
- then it calls a client actor to get the result. Result is also converted to json data
  using JsonCombinators
- client actor actually calls the backend services (thrift) and does some (very light)
  computations and send the result back to the controller


## Authentication

Currently Higgs support only Facebook token based authentication, wherein user calls
```GET /v1/oauth/token``` with authenticated facebook token and in return receives a higgs
access token to be used in further api requests. Higgs access token needs to be passed
as request parameter (HTTP header based tokens will be implemented soon).

Higgs Access tokens are [Json Web Tokens](http://jwt.io/) which encodes claims signed by secret
RSA public and private keys. This eliminates the need for accessing database to get user id
corresponding to a token (as is the case with cookie) and also keeps the application stateless.
These Access tokens are also time limited.

Access token can also be encrypted further, and allow roll overs. To be done in future.


## Future Tasks

- Make request json more flat and remove redundancy with the current combinators
- Move backend services to Akka Cluster, with higgs also part of the same cluster as a frontend
  node. Although some backend services will have separate cluster in those cases use ClusterClient
- Above will remove the need for thrift based communication or SOA architecture. And will
  fascilitate the move towards completely Reactive Design through remote actors. Exciting Yeah !!
- Better handling of request failures
- Move to complete OAuth 2.0 authentication model
- Using websockets for feed, search and shopplan (for updates) - another drive towards reactive
  design