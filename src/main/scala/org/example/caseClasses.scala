package org.example

case class Event(timestamp: Long, key: Key)

case class Key(id: Int, other: Int) {

  override def equals(objectVar: Any): Boolean = {
    objectVar match {
      case key: Key => id == key.id
      case _        => false
    }
  }

  override def hashCode: Int = {
    id
  }
}

case class Output(key: Key, count: Int)
