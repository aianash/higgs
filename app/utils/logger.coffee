winston = require 'winston'

logger = new (winston.Logger)(
  transports: [
    new (winston.transports.Console)(),
    new (winston.transports.File)(filename: 'higgs-service.log')
  ]
  exceptionHandlers: [
    new (winston.transports.File)(filename: 'higgs-fatal-error.log')
  ]
  exitOnError: false
)

module.exports = logger