var nconf = require('nconf')

nconf.argv()
     .env()

var env = nconf.get('NODE_ENV')
var appDir = nconf.get('APP_DIR')

require(__dirname + "/build/app")