package com.ngs.yibi.baseconfig

//import com.ngs.yibi.baseconfig.BaseConfig.baseconf
import com.typesafe.config.{Config, ConfigFactory}

object BaseConf {
  val baseconf: Config = ConfigFactory.load()

  lazy val baseHttpHost = baseconf.getString("akka.host")
  lazy val baseHttpPort = baseconf.getInt("akka.port")

  lazy val baseKeyspace = baseconf.getString("cassandra.keyspace")
  lazy val baseColumnfamily = baseconf.getString("cassandra.columnfamily")
}

trait BaseConf {
  import com.ngs.yibi.baseconfig.BaseConf._

  def httpHost = baseHttpHost
  def httpPort = baseHttpPort

  def keyspace = baseKeyspace
  def columnfamily = baseColumnfamily

}


/*
trait YibiConfig extends  {

  def conf:YibiConf

  def httpHost = conf.httpHost
  def httpPort = conf.httpPort

  def keyspace = conf.keyspace
  def columnfamily = conf.columnfamily

}
*/