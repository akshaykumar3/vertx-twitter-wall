package io.vertx.scala.tw.entity

/**
  * Request token entity.
  *
  * @param consumerKey    see Twitter API
  * @param consumerSecret see Twitter API
  */
case class RequestToken(consumerKey: String, consumerSecret: String)

/**
  * The result of Twitter OAuth 2 authentication.
  *
  * @param tokenType   token type, "bearer" in common
  * @param accessToken access token, which is used to access APIs
  */
case class Token(tokenType: String, accessToken: String)

