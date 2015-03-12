package net.magik6k.autoshoot

import akka.actor.{Props, Actor}

case object EntityUpdate
case object PositionRequest
case class Go(by: Int)
case class Rotate(by: Float)
case class EntityPosition(x: Int, y: Int, rotation: Float)


/**
 * Created by magik6k on 3/10/15.
 */
class Entity extends Actor {
  var x: Int = 100
  var y: Int = 100
  var rotation: Float = 0
  val ai = context.actorOf(Props[Ai])

  def checkPosition(): Unit = {
    if (x < 0) x = 0
    if (x > 800) x = 800
    if (y < 0) y = 0
    if (y > 600) y = 600
  }

  override def receive = {
    case EntityUpdate => ai ! AiUpdate(x, y); checkPosition()
    case PositionRequest => sender ! new EntityPosition(x, y, rotation)
    case action: Go => y += action.by; checkPosition()
    case action: Rotate => rotation += action.by; checkPosition()
    case _ => println("Illegal entity action")
  }
}
