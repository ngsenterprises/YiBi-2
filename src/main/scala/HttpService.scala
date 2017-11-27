package com.ngs.yibi.httpservice

import scala.concurrent.Future
import scala.io.StdIn
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import com.ngs.yibi.routes.UserRoutes
import com.ngs.yibi.dao.Dao
import com.ngs.yibi.akkautil.AkkaUtil
import com.ngs.yibi.util.Util
import com.ngs.yibi.html.HtmlContent


trait HttpService extends AkkaUtil with Util with HtmlContent with UserRoutes with Dao {

  createKeyspace( keyspace )
  createTable( s"${keyspace}.${columnfamily}" )
  //println( s"${keyspace}.${columnfamily}" )

  val serverBindingFuture: Future[ServerBinding] = Http().bindAndHandleAsync( requestHandler, httpHost, httpPort )

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()

  closeSystem

  serverBindingFuture
    .flatMap( _.unbind() )
    .onComplete { done =>
      done.failed.map { ex => println(ex, "Failed unbinding") }
      system.terminate()
    }

}
