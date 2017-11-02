package Cluster

import Shop.{ShardedCustomers, ShoppingBasketService}
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Main extends App with ShoppingBasketService{
  val conf = ConfigFactory.load
  implicit val system = ActorSystem("customers", conf)

  val customers = system.actorOf(ShardedCustomers.props, ShardedCustomers.name)

  val s = system.settings.config.getStringList("akka.cluster.roles")

  if(system.settings.config.getStringList("akka.cluster.roles").contains("seed")) {
    startService(customers)
  }

  system.actorOf(Props(new ClusterDomainEventListener), "cluster-listener")
}
