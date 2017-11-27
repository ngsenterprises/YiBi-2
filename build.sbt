
lazy val appVersion = "1.0.0"
lazy val appName = "yibi"
lazy val appOrganization = "com.ngs"
lazy val scalaversion = "2.12.4"

lazy val akkaHttpVersion = "10.0.9"
lazy val akkaVersion    = "2.5.3"

lazy val scalaTestVersion = "3.0.1"
lazy val akkaTestkitVersion = "2.5.6"

lazy val cassandraVersion= "2.1.6"
lazy val phantomVersion = "2.15.5"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"

parallelExecution in Test := false

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    inThisBuild( List(
      version         := appVersion,
      organization    := appOrganization,
      scalaVersion    := scalaversion,
      parallelExecution in Test := false
    ) ),
    name := appName,
    libraryDependencies ++= Seq(

      "com.typesafe.akka" %% "akka-actor"           % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,

      "com.datastax.cassandra"  % "cassandra-driver-core"   % cassandraVersion,
      "com.outworkers"          %% "phantom-dsl"            % phantomVersion,

      macwire,

      "commons-codec" % "commons-codec" % "1.11",
      "joda-time"     % "joda-time"     % "2.9.9",

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion     % Test,
      "org.scalatest"     %% "scalatest"            % scalaTestVersion    % "test, it",
      "com.typesafe.akka" %% "akka-testkit"         % akkaTestkitVersion  % Test

    )
  )

        