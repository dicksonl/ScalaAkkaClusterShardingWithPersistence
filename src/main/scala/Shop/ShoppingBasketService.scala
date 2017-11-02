package Shop

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.stream.ActorMaterializer

import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.Http

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, FiniteDuration}

trait ShoppingBasketService{
    def startService(customers: ActorRef)(implicit system: ActorSystem): Unit ={

      val config = system.settings.config
      val host = config.getString("http.host")
      val port = config.getInt("http.port")
      val t = config.getString("akka.http.server.request-timeout")
      val d = Duration(t)

      implicit val ec = system.dispatcher

      val api = new ShoppingBasketApi(customers, system, FiniteDuration(d.length, d.unit)).routes

      implicit val materializer = ActorMaterializer()

      val bindingFuture : Future[ServerBinding] = Http().bindAndHandle(api, host, port)

      val log =  Logging(system.eventStream, "shoppers")

      bindingFuture.map { serverBinding =>
        log.info(s"Shoppers API bound to ${serverBinding.localAddress} ")
      }
    }
}