package Shop

import akka.actor.{Props, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import scala.concurrent.duration.Duration

/**
  * Created by dickson.lui on 02/11/2017.
  */

object ShardedCustomer {
  def props = Props(new ShardedCustomer)
  def name(shopperId: Long) = shopperId.toString

  case object StopShopping

  val shardName: String = "customers"

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case cmd: Customer.Command => (cmd.customerId.toString, cmd)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case cmd: Customer.Command => (cmd.customerId % 12).toString
  }
}

class ShardedCustomer extends Customer {
  import ShardedCustomer._

  val timeOutDuration = context.system.settings.config.getString("passivate-timeout")

  context.setReceiveTimeout(Duration(timeOutDuration))

  override def unhandled(msg: Any) = msg match {
    case ReceiveTimeout =>
      context.parent ! Passivate(stopMessage = ShardedCustomer.StopShopping)
    case StopShopping => context.stop(self)
  }
}