package Shop

import akka.actor._
import akka.persistence._

/**
  * Created by dickson.lui on 02/11/2017.
  */
object Basket {
  def props = Props(new Basket)
  def name(shopperId: Long) = s"basket_${shopperId}"

  sealed trait Command extends Customer.Command
    case class Add(item: Item, customerId: Long) extends Command
    case class RemoveItem(productId: String, customerId: Long) extends Command
    case class Clear(customerId: Long) extends Command
    case class GetItems(customerId: Long) extends Command

    case class CountRecoveredEvents(customerId: Long) extends Command
    case class RecoveredEventsCount(count: Long)

    sealed trait Event extends Serializable
    case class Added(item: Item) extends Event
    case class ItemRemoved(productId: String) extends Event
    case class Cleared(clearedItems : Items) extends Event

    case class Snapshot(items: Items)
}

class Basket extends PersistentActor with ActorLogging {
  import Basket._

  var items = Items()
  var nrEventsRecovered = 0

  override def persistenceId = s"${self.path.name}"

  def receiveRecover = {
    case event: Event =>
      nrEventsRecovered = nrEventsRecovered + 1
      updateState(event)
    case SnapshotOffer(_, snapshot: Basket.Snapshot) =>
      log.info(s"Recovering baskets from snapshot: $snapshot for $persistenceId")
      items = snapshot.items
  }

  def receiveCommand = {

    case Add(item, _) =>
      persist(Added(item))(updateState)

    case RemoveItem(id, _) =>
      if(items.containsProduct(id)) {
        persist(ItemRemoved(id)){ removed =>
          updateState(removed)
          sender() ! Some(removed)
        }
      } else {
        sender() ! None
      }

    case Clear(_) =>
      persist(Cleared(items)){ e =>
        updateState(e)
        saveSnapshot(Basket.Snapshot(items))
      }

    case GetItems(_) =>
      sender() ! items

    case CountRecoveredEvents(_) =>
      sender() ! RecoveredEventsCount(nrEventsRecovered)

    case SaveSnapshotSuccess(metadata) =>
      log.info(s"Snapshot saved with metadata $metadata")

    case SaveSnapshotFailure(metadata, reason) =>
      log.error(s"Failed to save snapshot: $metadata, $reason.")
  }

  private val updateState: (Event => Unit) = {
    case Added(item)             => items = items.add(item)
    case ItemRemoved(id)         => items = items.removeItem(id)
    case Cleared(clearedItems)   => items = items.clear
  }
}