package com.ngs.yibi.dao

import com.datastax.driver.core.{Cluster, ResultSet, Session}

import scala.util.{Failure, Success, Try}

import org.joda.time.DateTime
import com.ngs.yibi.util._


trait Dao extends Util {

  def daoCnx =
    {
      for {
        bdr <- Try( Cluster.builder )
        ahost <- Try( bdr.addContactPoint( httpHost ) )
        cluster <- Try( ahost.build )
        session <- Try( cluster.connect )
      } yield ( (cluster, session) )
    } match {
      case Failure( f ) => throw new RuntimeException("failed to create cassandra Cluster.")
      case Success( p ) => p
    }
  def cluster = daoCnx._1
  def session = daoCnx._2

  def closeSystem(): Unit = cluster.close()

  def insertUrl( url: String, seedid: Long, ks_table: String ): ResultSet = {
    val dt = new DateTime()
    insertUrl( dt.getYear(), dt.getMonthOfYear(), seedid, url, ks_table )
  }
  def insertUrl( year: Int, month: Int, seedid: Long, url: String, ks_table: String ): ResultSet =
    session.execute(s"INSERT INTO ${ks_table} ( year, month, seedid, orgurl, posttime ) " +
      s"VALUES( ${year}, ${month}, ${seedid}, '${url}', toTimestamp(now()) )")

  def buildAndInsert( url: String, ks_table: String ): String = {
    val dt = new DateTime()
    getLargestRecordAdded( ks_table ) match {
      case None =>
        insertUrl( dt.getYear, dt.getMonthOfYear, 0L, url, ks_table )
        urlEncode( 0L )
      case Some( n ) =>
        val rs = insertUrl( dt.getYear, dt.getMonthOfYear, n.toLong + 1L, url, ks_table )
        urlEncode( n.toLong + 1L )
    }
  }

  def getUrl( id: Long, ks_table: String ): Option[String] = {
    val dt = new DateTime()
    var indexYear = dt.getYear()
    var indexMonth = dt.getMonthOfYear()
    var res = Option.empty[String]
    while ( res == Option.empty[String] && 2017 <= indexYear && 0 <= indexMonth ) {

      //println( s"getUrl year ${indexYear}, month ${indexMonth}" )

      session.execute( s"SELECT orgurl FROM ${ks_table} WHERE " +
        s"seedid = ${id} AND " +
        s"year = ${indexYear} AND " +
        s"month = ${indexMonth} " +
        s"ALLOW FILTERING" ) match {
          case rs if ( rs.isExhausted ) =>
            if ( indexMonth == 0 ) {
              indexMonth = 11
              indexYear -= 1
            } else indexMonth -= 1
          case rs => Try( rs.one.getString( "orgurl" ) ) match {
            case Success( s ) => res = Some( s )
            case Failure( e ) =>
              if ( indexMonth == 0 ) {
                indexMonth = 11
                indexYear -= 1
              } else indexMonth -= 1
          }
        }
    }
    res
  }

  def getId( url: String, ks_table: String ): Option[Long] = {
    val dt = new DateTime()
    var indexYear = dt.getYear()
    var indexMonth = dt.getMonthOfYear()
    var res = Option.empty[Long]
    while ( res == Option.empty[Long] && 2017 <= indexYear && 0 <= indexMonth ) {

      //println( s"getId indexYear ${indexYear}, indexMonth ${indexMonth}" )

      session.execute(s"SELECT seedid FROM ${ks_table} WHERE " +
        s"orgurl = '${url}' AND " +
        s"year = ${indexYear} AND " +
        s"month = ${indexMonth} " +
        s" ALLOW FILTERING" ) match {
          case rs if (rs.isExhausted) =>
            if (indexMonth == 0) {
              indexMonth = 11
              indexYear -= 1
            } else indexMonth -= 1
          case rs => Try(rs.one.getLong("seedid")) match {
            case Success(n) => res = Some(n)
            case Failure(e) => None
              if (indexMonth == 0) {
                indexMonth = 11
                indexYear -= 1
              } else indexMonth -= 1
          }
        }
    }
    res
  }

  def getLargestRecordAdded( table: String ): Option[Long] = {
    val dt = new DateTime()
    var indexYear = dt.getYear()
    var indexMonth = dt.getMonthOfYear()
    var found = false
    var res = Option.empty[Long]
    while (res == Option.empty[Long] && 2017 <= indexYear && 0 <= indexMonth) {

      //println( s"getLargestRecordAdded indexYear ${indexYear}, indexMonth ${indexMonth}" )

      session.execute(s"SELECT seedid FROM ${table} WHERE " +
        s"year = ${indexYear} AND " +
        s"month = ${indexMonth} " +
        s"ORDER BY seedid DESC LIMIT 1") match {
          case rs: ResultSet if (rs.isExhausted) =>
            if (indexMonth == 0) {
              indexMonth = 11
              indexYear -= 1
            } else indexMonth -= 1
          case rs: ResultSet => Try(rs.one.getLong("seedid")) match {
            case Success(n) => res = Some(n)
            case Failure(e) =>
              if (indexMonth == 0) {
                indexMonth = 11
                indexYear -= 1
              } else indexMonth -= 1
          }
        }
    }
    res
  }

  def createKeyspace( ks: String, replication: Int=1 ): ResultSet = {
    val keyspace = s"CREATE KEYSPACE IF NOT EXISTS ${ks} WITH replication = {'class':'SimpleStrategy', 'replication_factor':${replication} }"
    //println( s"createKeyspace keyspace: [${keyspace}]")
    session.execute( keyspace )
  }

  def createTable( ks_cf: String ): ResultSet = {
    session.execute(
      s"CREATE TABLE IF NOT EXISTS ${ks_cf} " +
        "( year INT, month INT, seedid BIGINT, orgurl TEXT, posttime TIMESTAMP, PRIMARY KEY ( ( year, month ), seedid, orgurl ) )" +
        " WITH CLUSTERING ORDER BY ( seedid DESC )"
    )
  }

}
