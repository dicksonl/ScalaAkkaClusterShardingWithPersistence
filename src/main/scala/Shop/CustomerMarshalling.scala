package Shop

import spray.json._

/**
  * Created by dickson.lui on 02/11/2017.
  */

trait CustomerMarshalling extends DefaultJsonProtocol {
  implicit val basketItemFormat: RootJsonFormat[Item] = jsonFormat3(Item)

  implicit val itemNumberFormat: RootJsonFormat[ItemNumber] = jsonFormat1(ItemNumber)

  implicit val basketFormat: RootJsonFormat[Items] = jsonFormat((list: List[Item]) => Items.aggregate(list), "items")
}

object CustomerMarshalling extends CustomerMarshalling