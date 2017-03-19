package io.vertx.scala.tw.entity

import io.vertx.lang.scala.json.JsonObject

/**
  * Tweet data object.
  *
  * @author Eric Zhao
  */
case class Tweet(user: JsonObject, text: String, time: String) {
  override def toString: String = {
    encode.encodePrettily
  }

  def encode: JsonObject = {
    new JsonObject().put("user", user)
      .put("text", text)
      .put("time", time)
  }
}

object Tweet {
  def fromJson(json: JsonObject): Tweet = Tweet(
    user = json.getJsonObject("user"),
    text = json.getString("text"),
    time = json.getString("created_at")
  )
}
