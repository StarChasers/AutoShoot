package net.magik6k.autoshoot

import akka.actor.{ Actor, ActorSystem, Props }
import akka.kernel.Bootable
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.{Gdx, ApplicationListener}
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import org.lwjgl.opengl.GL11

case object Start

class HelloActor extends Actor {
  val worldActor = context.actorOf(Props[WorldActor])

  def receive = {
    case Start => worldActor ! "Hello"
    case message: String =>
      println("Received message '%s'" format message)
  }
}

class WorldActor extends Actor {
  def receive = {
    case message: String => sender() ! (message.toUpperCase + " world!")
  }
}

object AutoShooter extends ApplicationListener {
  var camera:OrthographicCamera = null
  var batch:SpriteBatch = null
  var char:Texture = null

  override def create(): Unit = {
    println("Create")

    camera = new OrthographicCamera
    camera.setToOrtho(false, 800, 600)

    batch = new SpriteBatch

    char = new Texture(Gdx.files.internal("net/magik6k/char.png"))

}

  override def resize(width: Int, height: Int) {}

  override def dispose() {}

  override def pause() {}

  override def render(): Unit = {
    GL11.glClearColor(0, 0, 0.2f, 1)
    GL11.glClear(GL20.GL_COLOR_BUFFER_BIT)

    batch.setProjectionMatrix(camera.combined)
    batch.begin()
    batch.draw(char, 32, 32)
    batch.end()

    camera.update()
  }

  override def resume() {}
}

class HelloKernel extends Bootable {
  val system = ActorSystem("hellokernel")

  def startup = {
    system.actorOf(Props[HelloActor]) ! Start

    val gdxConfig = new LwjglApplicationConfiguration()
    gdxConfig.width = 800
    gdxConfig.height = 600
    gdxConfig.title = "AutoShooter"
    new LwjglApplication(AutoShooter, gdxConfig)
  }

  def shutdown = {
    system.shutdown()
  }
}

