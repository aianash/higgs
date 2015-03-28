Q     = require 'q'
_     = require 'lodash'
path  = require 'path'

common_ttypes   = require path.join(__dirname, '../lib/common_types')
neutrino_ttypes = require path.join(__dirname, '../lib/neutrino_types')
feed_ttypes     = require path.join(__dirname, '../lib/feed_types')


createFilter = module.exports.createFilter = (filter) ->
  {city, page} = filter

  location = new common_ttypes.PostalAddress {city} if city

  new feed_ttypes.FeedFilter {location, page}


toFlattenedOffer = module.exports.toFlattenedOffer = (offerPost) ->
  {
    ptuid   : offerPost.postId.ptuid
    idx     : offerPost.index
    from    :
      stuid : offerPost.storeId.stuid
      name  : offerPost.storeName.full
      handle: offerPost.storeName.handle
    address : offerPost.storeAddress.title
    title   : offerPost.offer.title
    subtitle: offerPost.offer.subtitle
  }


toFlattenedPosterAd = module.exports.toFlattenedPosterAd = (posterAdPost) ->
  {
    ptuid   : posterAdPost.postId.ptuid
    idx     : posterAdPost.index
    paduid  : posterAdPost.poster.paduid
    image   : posterAdPost.poster.image.link
  }


mergeTransform = module.exports.mergeTransform = (feed) ->
  offers    = _.map feed.offerPosts,    toFlattenedOffer
  posterAds = _.map feed.posterAdPosts, toFlattenedPosterAd

  feed = []
  feed = feed.concat offers.concat posterAds

  _.sortBy feed, 'idx'