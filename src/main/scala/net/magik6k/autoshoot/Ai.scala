package net.magik6k.autoshoot

import akka.actor.{ActorRef, Actor}

case class AiInitialize (body: ActorRef)
case class AiUpdate (x: Int, y: Int)

/**
 * Created by magik6k on 3/10/15.
 */
class Ai extends Actor {
  var back = false

  override def receive = {
    case info: AiInitialize => println("Ai Init")
    case update: AiUpdate => {
      back = if(update.y > 500) true else if(update.y < 100) false else back
      sender ! Go(if(back) -1 else 1)
    }
  }
}
