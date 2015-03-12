package net.magik6k.autoshoot

import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import akka.pattern.ask
import scala.collection.mutable
import akka.kernel.Bootable
import akka.util.Timeout
import scala.concurrent.duration._
import com.badlogic.gdx.graphics.g2d.{TextureRegion, SpriteBatch}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.{Gdx, ApplicationListener}
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import org.lwjgl.opengl.GL11

import scala.concurrent.Await

case object Start

class AutoShooter(actorSystem: ActorSystem) extends ApplicationListener {
  var camera:OrthographicCamera = null
  var batch:SpriteBatch = null
  var char:Texture = null

  val bodies = mutable.MutableList.empty[ActorRef]
  var lastTick: Long = 0

  override def create: Unit = {
    println("Create")

    bodies += actorSystem.actorOf(Props[Entity])

    for(body <- bodies) body ! Start

    camera = new OrthographicCamera
    camera.setToOrtho(false, 800, 600)
    batch = new SpriteBatch
    char = new Texture(Gdx.files.internal("net/magik6k/char.png"))

}

  override def resize(width: Int, height: Int) {}

  override def dispose: Unit = {
    char.dispose
    batch.dispose
  }

  override def pause {}

  override def render: Unit = {
    GL11.glClearColor(0, 0, 0.2f, 1)
    GL11.glClear(GL20.GL_COLOR_BUFFER_BIT)

    batch.setProjectionMatrix(camera.combined)

    while(System.nanoTime()/1000 - lastTick > (1000000/100)) {
      for(body <- bodies) body ! EntityUpdate
      lastTick += (1000000/100)
    }

    implicit val timeout = Timeout(500 millis)
    batch.begin
    for(body <- bodies) {
      val position: EntityPosition = Await.result(body ? PositionRequest, timeout.duration).asInstanceOf[EntityPosition]
      //println("y '%d'" format position.y)
      batch.draw(new TextureRegion(char), position.x,  position.y, 32, 32, 64, 64, 1, 1, position.rotation)
    }
    batch.end

    camera.update
  }

  override def resume {}
}

class HelloKernel extends Bootable {
  val system = ActorSystem("hellokernel")


  def startup = {

    val gdxConfig = new LwjglApplicationConfiguration()
    gdxConfig.width = 800
    gdxConfig.height = 600
    gdxConfig.title = "AutoShooter"
    new LwjglApplication(new AutoShooter(system), gdxConfig)
  }

  def shutdown = {
    system.shutdown()
  }
}

