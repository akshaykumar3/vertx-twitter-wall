import sbt._

object Version {
  final val Scala = "2.12.1"
  final val Vertx = "3.4.1"
}

object Library {
  val vertxCodegen = "io.vertx" % "vertx-codegen" % Version.Vertx % "provided"
  val vertxSockJsProxy = "io.vertx" % "vertx-sockjs-service-proxy" % Version.Vertx
  val vertxLangScala = "io.vertx" %% "vertx-lang-scala" % Version.Vertx
  val vertxWeb = "io.vertx" %% "vertx-web-scala" % Version.Vertx
  val vertxWebClient = "io.vertx" %% "vertx-web-client-scala" % Version.Vertx
  val vertxOAuth2 = "io.vertx" %% "vertx-auth-oauth2-scala" % Version.Vertx
}