include 'common.thrift'
include 'shopplan.thrift'
include 'feed.thrift'

namespace java com.goshoplane.neutrino.service
namespace js neutrino.service

typedef string FBToken

exception NeutrinoException {
  1: string message;
}


struct ModifyShopPlanReq {
  1: set<common.StoreId> stores;
  2: set<common.CatalogueItemId> items;
  3: set<common.UserId> invites;
}


struct AddDestinationReq {
  1: common.GPSLocation location;
  2: shopplan.DestinationOrder order;
}


struct UpdateDestinationReq {
  1: shopplan.DestinationId destId;
  2: optional common.GPSLocation location;
  3: optional shopplan.DestinationOrder order;
}


struct FacebookInfo {
  1: common.UserId userId
  2: FBToken token
}

struct UserInfo {
  1: optional common.UserId userId;
  2: optional common.UserName names;
  3: optional common.Locale locale;
  4: optional common.Gender gender;
  5: optional FacebookInfo facebookInfo;
  6: optional common.Email email;
  7: optional common.Timezone timezone;
  8: optional common.UserAvatar avatar;
  9: optional bool isNew;
}


service Neutrino {

  #/** User APIs */
  UserInfo createOrUpdateUser(1:UserInfo userInfo) throws (1:NeutrinoException nex);
  UserInfo getUserDetail(1:common.UserId userId) throws (1:NeutrinoException nex);


  #/** ShopPlan CRUD apis */
  list<shopplan.ShopPlan> getShopPlansFor(1:common.UserId userId) throws (1:NeutrinoException nex);
  shopplan.ShopPlan getShopPlan(1:shopplan.ShopPlanId shopplanId) throws (1:NeutrinoException nex);
  shopplan.ShopPlan newShopPlanFor(1:common.UserId userId) throws (1:NeutrinoException nex);
  bool endShopPlan(1:shopplan.ShopPlanId shopplanId) throws (1:NeutrinoException nex);

  bool addToShopPlan(1:shopplan.ShopPlanId shopplanId, 2:ModifyShopPlanReq addReq) throws (1:NeutrinoException nex);
  bool removeFromShopPlan(1:shopplan.ShopPlanId shopplanId, 2:ModifyShopPlanReq removeReq) throws (1:NeutrinoException nex);

  set<shopplan.Friend> getInvitedUsers(1:shopplan.ShopPlanId shopplanId) throws (1:NeutrinoException nex);

  set<common.GPSLocation> getStoreLocations(1:shopplan.ShopPlanId shopplanId) throws (1:NeutrinoException nex);

  set<shopplan.Destination> getDestinations(1:shopplan.ShopPlanId shopplanId) throws (1:NeutrinoException nex);
  set<shopplan.Destination> addDestinations(1:shopplan.ShopPlanId shopplanId, 2:set<AddDestinationReq> addReqs) throws (1:NeutrinoException nex);
  bool updateDestinations(1:set<UpdateDestinationReq> updateReqs) throws (1:NeutrinoException nex);
  bool removeDestinations(1:set<shopplan.DestinationId> destIds) throws (1:NeutrinoException nex);

  #/** Search APIs */



  #/** Feed APIs */
  feed.Feed getCommonFeed(1:feed.FeedFilter filter) throws (1:NeutrinoException nex);
  feed.Feed getUserFeed(1:common.UserId userId, 2:feed.FeedFilter filter) throws (1:NeutrinoException nex);


  #/** Messaging APIs */


}