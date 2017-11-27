package com.ngs.yibi.routesspecs

//com.ngs.yibi.routesspecs.UserRoutesSpec

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.Http
import akka.util.ByteString

import com.ngs.yibi.baseconfig._
import com.ngs.yibi.akkautil.AkkaUtil
import com.ngs.yibi.dao.Dao

import org.scalatest.FlatSpec
import com.ngs.yibi.httpservice.HttpService


import com.softwaremill.macwire._

object testObj extends App with HttpService

class UserRoutesSpec extends FlatSpec with BaseConf with AkkaUtil with Dao {

  "default GET" should "return the home page" in {

    val testuri = s"http://${httpHost}:${httpPort}"

    val fut: Future[HttpResponse] = Http().singleRequest(HttpRequest(GET, uri = testuri))

    fut.onComplete {
      case Failure( f ) => assert( false )
      case Success( s ) if ( s.status == StatusCodes.OK ) => assert( true )
    }

    //val res = Await.ready( fut, Duration.Inf )

  }

  "POST" should "insert the url." in {

    val testuri = s"http://${httpHost}:${httpPort}/urls"

    val rawurl = """www.allitebooks.com/"""
    val surl = """http://""" +rawurl

    val fut: Future[HttpResponse] =
      Http().singleRequest(
        HttpRequest(
          POST,
          uri = testuri,
          entity=HttpEntity.Strict(
            ContentTypes.`text/plain(UTF-8)` ,
            akka.util.ByteString( surl )
            //ByteString(117, 114, 108, 61, 104, 116, 116, 112, 37, 51, 65, 37, 50, 70, 37, 50, 70, 119, 119, 119, 46, 97, 108, 108, 105, 116, 101, 98, 111, 111, 107, 115, 46, 99, 111, 109, 37, 50, 70)
          )
        )
      )

    fut.onComplete {
      case Failure( f ) => assert( false )
      case Success( s ) if ( s.status == StatusCodes.OK ) =>

        getId( rawurl, s"${keyspace}.${columnfamily}" ) match {

          case Success( s ) =>
        }




        assert( true )
    }

    //val res = Await.ready( fut, Duration.Inf )
    //println(s"res ${res}")

  }

}
