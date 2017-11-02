package Shop

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import akka.pattern.ask
import akka.actor._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.concurrent.ExecutionContext
import scala.util.Try

case class ItemNumber(number: Int)

class ShoppingBasketApi(
      val customers: ActorRef,
      val system: ActorSystem,
      val requestTimeout: Timeout) extends CustomersRoutes {
  val executionContext = system.dispatcher
}

trait CustomersRoutes extends CustomerMarshalling {
  def routes =
    deleteItem ~
      getBasket ~
      deleteBasket

  def customers: ActorRef

  implicit def requestTimeout: Timeout
  implicit def executionContext: ExecutionContext

  def getBasket = {
    get {
      pathPrefix("customer" / ShopperIdSegment / "basket") { customerId =>
        pathEnd {
          onSuccess(customers.ask(Basket.GetItems(customerId)).mapTo[Items]) {
            case Items(Nil)   => complete(NotFound)
            case items: Items => complete(items)
          }
        }
      }
    }
  }

  def deleteBasket = {
    delete {
      pathPrefix("customer" / ShopperIdSegment / "basket") { shopperId =>
        pathEnd {
          customers ! Basket.Clear(shopperId)
          complete(OK)
        }
      }
    }
  }

  def addItem = {
    post {
      pathPrefix("customer" / ShopperIdSegment / "basket") { shopperId =>
        pathEnd {
            entity(as[Item]) { item =>
              customers ! Basket.Add(item, shopperId)
              complete(OK)
            }
        }
      }
    }
  }

  def deleteItem = {
    delete {
      pathPrefix("customer" / ShopperIdSegment / "basket" / ProductIdSegment) {
        (shopperId, productId) =>
        pathEnd {
          val removeItem = Basket.RemoveItem(productId, shopperId)
          onSuccess(customers.ask(removeItem)
            .mapTo[Option[Basket.ItemRemoved]]) {
            case Some(_) => complete(OK)
            case None    => complete(NotFound)
          }
        }
      }
    }
  }

  val ShopperIdSegment = Segment.flatMap(id => Try(id.toLong).toOption)
  val ProductIdSegment = Segment.flatMap(id => if(!id.isEmpty) Some(id) else None)
}