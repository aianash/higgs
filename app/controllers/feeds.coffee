getCommonFeed = (req, res) ->
  res.send {message: "comming soon"}



getUserFeed = (req, res) ->
  res.send {message: "comming soon"}



exports.common = getCommonFeed
exports.user   = getUserFeed