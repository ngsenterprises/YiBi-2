package com.ngs.yibi.utiltest

//com.ngs.yibi.utiltest.UtilSpec

import org.scalatest.FlatSpec
import com.ngs.yibi.baseconfig._
import com.ngs.yibi.util._
import com.softwaremill.macwire._


class UtilSpec extends FlatSpec with Util {

  "For a shortened url, urlDecode( urlEncode )" should "return the url." in {

    val n = math.abs( scala.util.Random.nextLong )
    val res = urlDecode( urlEncode( n ) )

    assert( res.get == n )
  }

  "For a random long auto made uri and decoded extension, decodeLongFromUrl" should "equal the random long." in {

    val n = math.abs( scala.util.Random.nextLong )
    val ext = urlEncode( n )
    val res = decodeLongFromUrl( s"${yibiBase}/${urlEncode( n )}" )

    assert( res.get == n )
  }

}
