import sbt.Package._

name := "vertx-twitter-wall"
organization := "io.vertx"
version := "1.0"
scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  Library.vertxSockJsProxy,
  Library.vertxLangScala,
  Library.vertxCodegen,
  Library.vertxWeb,
  Library.vertxWebClient
)

packageOptions += ManifestAttributes(
  ("Main-Verticle", "scala:io.vertx.scala.tw.TwitterWallVerticle"))

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", xs@_*) => MergeStrategy.last
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
  case PathList("codegen.json") => MergeStrategy.discard
  case PathList("io", "vertx", "ext", "web", "package-info.class") => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
