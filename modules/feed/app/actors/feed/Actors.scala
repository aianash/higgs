package actors.feed

import play.api._
import play.api.libs.concurrent.Akka

import org.apache.thrift.protocol.TBinaryProtocol

import com.twitter.finagle.Thrift
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.thrift.ThriftClientFramedCodecFactory

import com.goshoplane.neutrino.service._
import com.goshoplane.creed.service._
import com.goshoplane.cassie.service._


/**
 * Companion object, primarily to access [[akka.actor.ActorRef]] of
 * client actors
 */
object Actors {

  private def actors(implicit app: Application) = app.plugin[Actors]
    .getOrElse(sys.error("Actors plugin not registered"))

  def feedClient(implicit app: Application) = actors.feedClient
}


/**
 * All service client and client actors to these service's are
 * creating using Play Plugin. This plugin's initialization order
 * is specified in `play.plugins` file
 */
class Actors(app: Application) extends Plugin {

  private def system = Akka.system(app)

  // Create a client to Neutrino service, which is
  // shared among the associated client actors
  private val neutrino = {
    val protocol = new TBinaryProtocol.Factory()

    val endpoint =
      (for {
        host <- app.configuration.getString("neutrino.host")
        port <- app.configuration.getInt("neutrino.port")
      } yield host + ":" + port) getOrElse "127.0.0.1:2424" // default endpoint

    val connectionLimit = app.configuration.getInt("neutrino.connection-limit").getOrElse(2)

    val client = ClientBuilder().codec(new ThriftClientFramedCodecFactory(None, false, protocol))
      .dest(endpoint).hostConnectionLimit(connectionLimit).build()

    new Neutrino$FinagleClient(client, protocol)
  }

  // lazily create a the feed client actor
  private lazy val feedClient   = system.actorOf(FeedClient.props(neutrino), "feedClient")
}