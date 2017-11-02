package Shop

import akka.actor.{Actor, Props}

object Customer {
  def props(customerId: Long) = Props(new Customer)
  def name(customerId: Long) = customerId.toString

  trait Command {
    def customerId: Long
  }

  case class ViewBasket(customerId: Long) extends Command
}

class Customer extends Actor {
  import Customer._

  def customerId = self.path.name.toLong

  val basket = context.actorOf(Basket.props,
    Basket.name(customerId))

  def receive = {
    case cmd: Basket.Command => basket forward cmd

    case ViewBasket(shopperId) => basket ! Basket.GetItems(shopperId)
  }
}