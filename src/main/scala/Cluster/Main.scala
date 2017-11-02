package Cluster

import Shop.{ShardedCustomers, ShoppingBasketService}
import akka.actor.ActorSystem

object Main extends App with ShoppingBasketService{
  implicit val system = ActorSystem("ShoppingCartExample")

  val customers = system.actorOf(ShardedCustomers.props, ShardedCustomers.name)

  startService(customers)
}
