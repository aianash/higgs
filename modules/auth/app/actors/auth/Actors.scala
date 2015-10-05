package actors.auth

import javax.inject._

import play.api._
import play.api.libs.concurrent.Akka
import play.api.inject._

import akka.actor.ActorSystem

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

import neutrino.auth._


class Actors extends AbstractModule with AkkaGuiceSupport {

  def configure = {
    bindActor[AuthService]("authService")
  }

}