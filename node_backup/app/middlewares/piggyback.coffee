_          = require 'lodash'
pathRegexp = require 'path-to-regexp'
bodyParser = require 'body-parser'
passport   = require 'passport'


jsonParser       = bodyParser.json()
authenticate     = passport.authenticate ['bearer'], {session: false}

class RouteConfigurator
  constructor: (@method, @path, @handlers) ->

  in: (router) ->
    router[@method.toLowerCase()](@path, @handlers)



class Router
  constructor: (@method, @path, @handler) ->
    @_reqexp = pathRegexp(@path, @_keys = [], {})


  dispatch: (method, path, req, res) ->
    @handler(req, res)



class Piggyback

  constructor: ->
    @_stack = []


  register: (method, path, handler, options) ->
    if not _.isFunction(handler)
      throw new Error('Currently we only support one handler which has to be function')

    options = options || {}
    _.defaults options, {authenticate: true}



    @_stack.push(new Router(method, path, handler))


    # [NOTE] Currently parser and authenticate middleware is
    # hard coded
    handlers = [jsonParser]
    if options.authenticate then handlers.push(authenticate)

    handlers = handlers.concat [@_handler, handler]


    new RouteConfigurator(method, path, handlers)



  _handler: (req, res, next) ->
    console.log "piggy handler, this will call all the matching routers"

    # caching the original parameters for final handler
    originalParam = req.params
    originalQuery = req.query
    originalBody  = req.body

    originalSend = res.send

    res.send = (body) ->
      console.log "following response of the handler will be recorded in piggy response instead of actually sending response"
      console.log body


    _.forEach @_stack, (router) -> router.dispatch(req, res)


    # restoring the original req params for the final handler
    req.params = originalParam
    req.query  = originalQuery
    req.body   = originalBody


    # encapsulated send
    res.send = (body) ->
      body._piggy_res = {
        piggy_txn_id: _.now()
        piggy_resps: []
      }

      originalSend.apply(res, [body])

    next()




module.exports = new Piggyback()