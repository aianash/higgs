package inject

import javax.inject._

import com.google.inject.name.Names

import akka.actor.{ActorRef, ActorSystem}

import com.google.inject.AbstractModule

import play.api.libs.concurrent.AkkaGuiceSupport
import play.api._

import actors._


class AppModule extends AbstractModule with AkkaGuiceSupport {

  def configure = {
    bindActorFactory[ClientConnection, ClientConnection.Factory]
  }

}