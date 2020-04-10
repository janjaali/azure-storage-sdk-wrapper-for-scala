package janjaali.akka

import akka.actor.ActorSystem
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

trait TestActorSystem extends Suite with BeforeAndAfterAll {

  protected implicit val actorSystem: ActorSystem = {

    val randomAlphanumericString = Random.alphanumeric.take(5).mkString

    ActorSystem(randomAlphanumericString)
  }

  override def afterAll(): Unit = {

    super.afterAll()

    actorSystem.terminate()

    try {
      Await.ready(actorSystem.whenTerminated, 15.seconds)
    } catch {
      case cause: Exception => println(s"Failed to terminate actorSystem '$actorSystem': $cause.")
    }
  }
}
