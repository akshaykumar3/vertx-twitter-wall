package io.vertx.scala.tw

import io.vertx.lang.scala.json.{JsonArray, JsonObject}
import io.vertx.scala.ext.web.client.WebClient
import io.vertx.scala.ext.web.codec.BodyCodec

import scala.concurrent.{ExecutionContext, Future}

trait TweetSearchService[R] {
  def search(q: String): Future[R]
}

/**
  * Tweet search service.
  *
  * @author Eric Zhao
  */
class TweetSearchServiceImpl(client: WebClient, token: String, implicit val executionContext: ExecutionContext) extends TweetSearchService[JsonArray] {

  override def search(q: String): Future[JsonArray] = {
    client.getAbs("https://api.twitter.com/1.1/search/tweets.json")
      .as(BodyCodec.jsonObject())
      .addQueryParam("q", s"%23$q")
      .putHeader("Authorization", s"Bearer $token")
      .sendFuture()
      .map(_.body)
      .flatMap(validate)
  }

  private def validate(res: JsonObject): Future[JsonArray] = Option(res.getJsonArray("errors")) match {
    case Some(err) => Future.failed(new Exception(err.getJsonObject(0).encodePrettily()))
    case None => Future.successful(res.getJsonArray("statuses"))
  }
}
