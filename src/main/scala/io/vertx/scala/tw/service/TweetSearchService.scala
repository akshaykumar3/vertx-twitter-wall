package io.vertx.scala.tw.service

import io.vertx.lang.scala.json.{JsonArray, JsonObject}
import io.vertx.scala.ext.web.client.WebClient
import io.vertx.scala.ext.web.codec.BodyCodec
import io.vertx.scala.tw.util.Logging

import scala.concurrent.{ExecutionContext, Future}

/**
  * A simple tweet search service trait.
  *
  * @tparam R type of result
  */
trait TweetSearchService[R] {
  def search(q: String): Future[R]
}

/**
  * Implementation of tweet search service (by hash tag).
  *
  * @author Eric Zhao
  */
class TweetSearchServiceImpl(client: WebClient, token: String, implicit val executionContext: ExecutionContext)
  extends TweetSearchService[JsonArray] with Logging {

  override def search(q: String): Future[JsonArray] = {
    client.getAbs("https://api.twitter.com/1.1/search/tweets.json")
      .as(BodyCodec.jsonObject())
      .timeout(8888L)
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
