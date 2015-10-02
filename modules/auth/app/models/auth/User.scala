package models.auth

import neutrino.core.user.UserId


/**
 * User object currently just holding user id
 *
 * @param id    UserId of user
 */
case class User(id: UserId)

