package com.ngs.yibi.daotest

//com.ngs.yibi.daotest.DaoSpec

import org.scalatest.FlatSpec
import com.ngs.yibi.baseconfig._
import com.ngs.yibi.dao.Dao
import com.softwaremill.macwire._

class DaoSpec extends FlatSpec with BaseConf with Dao {

  "A call to createKeyspace( keyspaceName )" should "create keyspaceName as a keySpace." in {

    createKeyspace( keyspace, 1 )

    val buf = scala.collection.mutable.ListBuffer.empty[String]
    session.execute( "SELECT * FROM system_schema.keyspaces" ) match {
      case rs if ( rs.isExhausted ) => throw new RuntimeException("rs.isExhausted")
      case rs => rs.forEach( r => buf += r.getString("keyspace_name") )
    }
    assert( buf.exists( _ == keyspace ) )

    session.execute( s"DROP KEYSPACE IF EXISTS ${keyspace}" )
  }

  "A call to createTable( tableName )" should "create a tableName as a table." in {

    createKeyspace( keyspace )
    createTable( s"${keyspace}.${columnfamily}" )

    val buf = scala.collection.mutable.ListBuffer.empty[String]
    session.execute( "select table_name from system_schema.tables" ) match {
      case rs if ( rs.isExhausted ) => throw new RuntimeException("rs.isExhausted")
      case rs => rs.forEach( r => buf += r.getString("table_name") )
    }
    assert( buf.exists( _ == columnfamily ) )

    session.execute( s"DROP TABLE IF EXISTS ${keyspace}.${columnfamily}" )
    session.execute( s"DROP KEYSPACE IF EXISTS ${keyspace}" )
  }


  "A call to the getLargestRecordAdded method, on an empty table" should "return an empty ResultSet." in {

    createKeyspace( keyspace )
    createTable( s"${keyspace}.${columnfamily}" )

    val res = getLargestRecordAdded( s"${keyspace}.${columnfamily}" )

    assert( res.isEmpty )

    session.execute( s"DROP TABLE IF EXISTS ${keyspace}.${columnfamily}" )
    session.execute( s"DROP KEYSPACE IF EXISTS ${keyspace}" )
  }

  "A call to the insertUrl method" should "insert an item." in {

    val index = 0L
    val url = "testurl"

    createKeyspace( keyspace )
    createTable( s"${keyspace}.${columnfamily}" )

    insertUrl( url, index, s"${keyspace}.${columnfamily}" )

    session.execute( s"select * from ${keyspace}.${columnfamily}" ) match {
      case rs if ( rs.isExhausted ) => throw new RuntimeException("Error: insertUrl.")
      case rs => assert( rs.one().getString("orgurl") == url )
    }

    session.execute( s"DROP TABLE IF EXISTS ${keyspace}.${columnfamily}" )
    session.execute( s"DROP KEYSPACE IF EXISTS ${keyspace}" )
  }

  "A call to the getLargestRecordAdded method, on a single item table" should "return one item ResultSet." in {

    val index = 0L
    val url = "testurl"

    createKeyspace( keyspace )
    createTable( s"${keyspace}.${columnfamily}" )
    insertUrl( url, index, s"${keyspace}.${columnfamily}" )

    val res = getLargestRecordAdded( s"${keyspace}.${columnfamily}" )

    assert( !res.isEmpty )
    res.forall( _ == 0L )

    session.execute( s"DROP TABLE IF EXISTS ${keyspace}.${columnfamily}" )
    session.execute( s"DROP KEYSPACE IF EXISTS ${keyspace}" )
  }

  "A call to the getLastRecordAdded method, on a muli item table" should "return one item ResultSet with largest seedid." in {

    createKeyspace( keyspace )
    createTable( s"${keyspace}.${columnfamily}" )
    insertUrl( "testurl_0", 0L, s"${keyspace}.${columnfamily}" )
    insertUrl( "testurl_1", 1L, s"${keyspace}.${columnfamily}" )

    val res = getLargestRecordAdded( s"${keyspace}.${columnfamily}" )

    assert( !res.isEmpty )
    res.forall( _ == 1L )

    session.execute( s"DROP TABLE IF EXISTS ${keyspace}.${columnfamily}" )
    session.execute( s"DROP KEYSPACE IF EXISTS ${keyspace}" )
  }

  "A call to the getUrl method, on a muli item table" should "return the item with indicated seedid." in {

    createKeyspace( keyspace )
    createTable( s"${keyspace}.${columnfamily}" )
    insertUrl( "testurl_0", 0L, s"${keyspace}.${columnfamily}" )
    insertUrl( "testurl_1", 1L, s"${keyspace}.${columnfamily}" )

    val res = getUrl( 1L, s"${keyspace}.${columnfamily}" )

    //println( s"res ${res}" )

    assert( !res.isEmpty )
    res.forall( _ == "testurl_1" )

    session.execute( s"DROP TABLE IF EXISTS ${keyspace}.${columnfamily}" )
    session.execute( s"DROP KEYSPACE IF EXISTS ${keyspace}" )
  }

  "A call to the getId method, on a muli item table" should "return the item with indicated url." in {

    createKeyspace( keyspace )
    createTable( s"${keyspace}.${columnfamily}" )
    insertUrl( "test_url_0", 0L, s"${keyspace}.${columnfamily}" )
    insertUrl( "test_url_1", 1L, s"${keyspace}.${columnfamily}" )

    val res = getId( "test_url_1", s"${keyspace}.${columnfamily}" )
    //println( res )

    assert( !res.isEmpty )
    res.forall( _ == 1L )

    session.execute( s"DROP TABLE IF EXISTS ${keyspace}.${columnfamily}" )
    session.execute( s"DROP KEYSPACE IF EXISTS ${keyspace}" )
  }

}

