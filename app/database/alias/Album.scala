package database.alias

case class Album(id: Option[Int] = None, name:String, interpret: String, fk_user: Int)