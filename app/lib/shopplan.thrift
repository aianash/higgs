include 'common.thrift'

namespace java com.goshoplane.neutrino.shopplan
namespace js neutrino.shopplan

typedef i16 DestinationOrder
typedef string Title
typedef i64 Timestamp

struct ShopPlanId {
  1: common.UserId createdBy;
  2: i64 suid;
}

struct DestinationId {
  1: ShopPlanId shopplanId;
  2: i64 dtuid;
}

enum BucketStoreField {
  Name              = 1;
  Address           = 2;
  ItemTypes         = 3;
  Avatar            = 4;
  Contacts          = 5;
  CatalogueItems    = 6;
  CatalogueItemIds  = 7;
}

struct BucketStore {
  1: common.StoreId storeId;
  2: common.StoreType storeType;
  3: common.StoreInfo info;
  4: optional list<common.JsonCatalogueItem> catalogueItems;
}

enum ShopPlanStoreField {
  Name             = 1;
  Address          = 2;
  ItemTypes        = 3;
  Avatar           = 4;
  Contacts         = 5;
  CatalogueItemIds = 6;
  CatalogueItems   = 7;
}

struct ShopPlanStore {
  1: common.StoreId storeId;
  2: DestinationId destId;
  3: common.StoreType storeType;
  4: common.StoreInfo info
  5: optional list<common.JsonCatalogueItem> catalogueItems;
  6: optional list<common.CatalogueItemId> itemIds;
}

struct Destination {
  1: DestinationId destId;
  2: common.PostalAddress address;
  3: optional i32 numShops;
}

enum InviteStatus {
  PENDING  = 1;
  INVITED  = 2;
  ACCEPTED = 3;
}

struct Friend {
  1: common.UserId id;
  2: optional common.UserName name;
  3: optional common.UserAvatar avatar;
}

struct Invite {
  1: common.UserId friendId;
  2: ShopPlanId shopplanId;
  3: optional common.UserName name;
  4: optional common.UserAvatar avatar;
  5: optional InviteStatus inviteStatus;
}

enum ShopPlanField {
  Title            = 1;
  Stores           = 2;
  CatalogueItems   = 3;
  CatalogueItemIds = 4;
  Destinations     = 5;
  Invites          = 6;
}

struct ShopPlan {
  1: ShopPlanId shopplanId;
  2: optional Title title;
  3: optional list<ShopPlanStore> stores;
  4: optional list<Destination> destinations;
  5: optional list<Invite> invites;
  6: bool isInvitation;
}