package io.vertx.scala.tw.util

import io.vertx.core.logging.{Logger, LoggerFactory}

/**
  * A simple trait for logging.
  */
trait Logging {
  protected lazy val logger: Logger = LoggerFactory.getLogger(getClass)
}
