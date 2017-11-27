package com.ngs.yibi.akkautil

import akka.actor.{ActorSystem}
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext

object AkkaUtil {
  implicit lazy val sys: ActorSystem = ActorSystem("YiBiUrlServer")
  implicit lazy val mat: ActorMaterializer = ActorMaterializer()
  implicit lazy val ec: ExecutionContext = sys.dispatcher
}


trait AkkaUtil {
  println("AkkaUtil")
  implicit val system: ActorSystem = AkkaUtil.sys
  implicit val materializer: ActorMaterializer = AkkaUtil.mat
  implicit val executionContext: ExecutionContext = AkkaUtil.ec
}
