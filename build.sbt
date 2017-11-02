name := "ScalaAkkaClusterShardingExample"

version := "1.0"

scalaVersion := "2.12.3"

scalaSource in Compile <<= baseDirectory(_ / "src/main/scala")

mainClass in (Compile, run) := Some("Cluster.Main")

mainClass in (Compile, packageBin) := Some("Cluster.Main")

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.12" % "2.5.6",
  "com.typesafe.akka" % "akka-slf4j_2.12" % "2.5.6",
  "com.typesafe.akka" % "akka-remote_2.12" % "2.5.6",
  "com.typesafe.akka" % "akka-cluster_2.12" % "2.5.6",
  "com.typesafe.akka" % "akka-http-spray-json_2.12" % "10.0.10",
  "com.typesafe.akka" % "akka-persistence_2.12" % "2.5.6",
  "com.typesafe.akka" % "akka-cluster-sharding_2.12" % "2.5.6"
)