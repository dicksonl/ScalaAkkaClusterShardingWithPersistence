package Shop

/**
  * Created by dickson.lui on 02/11/2017.
  */

import akka.actor._
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}

object ShardedCustomers {
  def props= Props(new ShardedCustomers)
  def name = "sharded-customers"
}

class ShardedCustomers extends Actor {

  ClusterSharding(context.system).start(
    ShardedCustomer.shardName,
    ShardedCustomer.props,
    ClusterShardingSettings(context.system),
    ShardedCustomer.extractEntityId,
    ShardedCustomer.extractShardId
  )

  def shardedCustomer = {
    ClusterSharding(context.system).shardRegion(ShardedCustomer.shardName)
  }

  def receive = {
    case cmd: Customer.Command =>
      shardedCustomer forward cmd
  }
}