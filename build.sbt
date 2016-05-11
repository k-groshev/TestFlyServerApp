name := "TestFlyServerApp"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.4",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2",
  "com.quantifind" %% "wisp" % "0.0.4"
)
