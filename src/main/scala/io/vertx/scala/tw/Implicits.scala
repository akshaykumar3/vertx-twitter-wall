package io.vertx.scala.tw

import java.util.Base64

import scala.util.Try

object Implicits {

  implicit class RequestTokenConverter(rtk: RequestToken) {
    def encode: String = s"${rtk.consumerKey.base64Encode}:${rtk.consumerSecret.base64Encode}"
  }

  implicit class StringImplicits(str: String) {
    def base64Decode: Try[String] = str match {
      case null => null
      case _ => Try(new String(Base64.getDecoder.decode(str), "UTF-8"))
    }

    def base64Encode: String = str match {
      case null => null
      case _ => Base64.getEncoder.encodeToString(str.getBytes())
    }
  }

}
