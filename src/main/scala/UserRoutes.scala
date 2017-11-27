package com.ngs.yibi.routes

import scala.concurrent.{Future, Await}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.Http

import com.ngs.yibi.html.HtmlContent
import com.ngs.yibi.dao.Dao
import com.ngs.yibi.util.Util
import com.ngs.yibi.akkautil.AkkaUtil
import com.ngs.yibi.baseconfig._
import akka.http.scaladsl.model.HttpRequest


trait UserRoutes extends AkkaUtil with HtmlContent with Dao {

  //defined in YiBiApp
  //def conf: BaseConf

  def baseAddress = s"${httpHost}:${httpPort}"
  def ks_table = s"${keyspace}.${columnfamily}"

  val requestHandler: HttpRequest => Future[HttpResponse] = {

    case HttpRequest( GET, _, _, _, _) =>
      Future( HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, getDefault( "YiBi" ) ) ) )

    case HttpRequest( POST, a, b, c, d ) if ( a.toString.contains( s"${baseAddress}/url" ) ) =>
      println( s"a ${a}")
      println( s"b ${b}")
      println( s"c ${c}")
      println( s"d ${d}")
      val url = c.asInstanceOf[HttpEntity.Strict].data.utf8String.split("=") match {
        case arr if ( arr.length < 2 ) => ""
        case arr => java.net.URLDecoder.decode( arr.last, "UTF-8").trim
      }
      responseFilter( url )

    case _ => Future( HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, getDefault( "YiBi" ) ) ) )
  }

  def responseFilter( url: String ): Future[HttpResponse] = {

    if (url.length == 0)
      Future(HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, getDefault("YiBi"))))
    else {

      decodeLongFromUrl(url) match {
        // in short format
        case Some(n) => getUrl(n, ks_table) match {
          //in the database
          //redirect
          case Some(urlRedirect) =>
            Future(HttpResponse(status = StatusCodes.PermanentRedirect, headers = headers.Location(urlRedirect) :: Nil,
              entity = StatusCodes.PermanentRedirect.htmlTemplate match {
                case "" => HttpEntity.Empty
                case template => HttpEntity(ContentTypes.`text/html(UTF-8)`, template format urlRedirect)
              })
            )
          case None =>
            val ext = buildAndInsert(url, ks_table)
            Future(HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, getResult("YiBi", s"${baseAddress}/${ext}"))))

        }
        // long format
        case None => getId(url, ks_table) match {
          //not in db
          case None => getLargestRecordAdded(ks_table) match {

            case None => //error
              insertUrl(url, 0L, ks_table)
              val ext = urlEncode(0L)
              Future(HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, getResult("YiBi", s"${baseAddress}/${ext}"))))

            case Some(n) => //got last record
              val rs = insertUrl(url, n + 1, ks_table)
              Future(HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, getResult("YiBi", s"${baseAddress}/${urlEncode(n + 1)}"))))
          }
          //in db
          case Some(n) =>
            Future(HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, getResult("YiBi", s"${baseAddress}/${urlEncode(n)}"))))
        }
      }
    }
  }
}
