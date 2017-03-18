package io.vertx.scala.tw

import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.web.client.WebClient
import io.vertx.scala.ext.web.codec.BodyCodec
import io.vertx.scala.tw.Implicits.RequestTokenConverter

import scala.concurrent.{ExecutionContext, Future}

/**
  * Twitter auth service trait.
  */
trait TwitterAuthService[T, R] {
  def auth(param: T): Future[R]
}

/**
  * Simple implementation of Twitter application-only authentication.
  *
  * @author Eric Zhao
  */
class TwitterAuthServiceImpl(client: WebClient, implicit val executionContext: ExecutionContext) extends TwitterAuthService[RequestToken, Token] {
  override def auth(rtk: RequestToken): Future[Token] = {
    client.postAbs("https://api.twitter.com/oauth2/token")
      .as(BodyCodec.jsonObject())
      .timeout(5000L)
      .addQueryParam("grant_type", "client_credentials")
      .putHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
      .putHeader("Authorization", s"Basic ${rtk.encode}")
      .sendFuture()
      .map(_.body)
      .flatMap(validateToken)
  }

  private def validateToken(res: JsonObject): Future[Token] = Option(res.getString("access_token")) match {
    case Some(accessToken) => Future.successful(Token(res.getString("token_type"), accessToken))
    case None => Future.failed(new Exception(res.getJsonArray("errors").getJsonObject(0).encodePrettily()))
  }
}
