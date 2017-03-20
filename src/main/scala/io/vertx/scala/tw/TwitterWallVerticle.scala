package io.vertx.scala.tw

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.lang.scala.json.JsonArray
import io.vertx.scala.ext.web.client.{WebClient, WebClientOptions}
import io.vertx.scala.ext.web.handler.StaticHandler
import io.vertx.scala.ext.web.handler.sockjs.{BridgeOptions, PermittedOptions, SockJSHandler}
import io.vertx.scala.ext.web.{Router, RoutingContext}
import io.vertx.scala.tw.entity.RequestToken
import io.vertx.scala.tw.service.{TweetSearchService, TweetSearchServiceImpl, TwitterAuthServiceImpl}

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * The verticle serving Twitter Wall sample.
  *
  * @author Eric Zhao
  */
class TwitterWallVerticle extends ScalaVerticle {

  private var hashTag = "vertx"
  private var searchService: TweetSearchService[JsonArray] = _

  private val SOCKJS_ADDRESS = "address.to.client"

  override def startFuture(): Future[Unit] = {
    val router = Router.router(vertx)
    val clientOptions = WebClientOptions().setSsl(true)
    val client = WebClient.create(vertx, clientOptions)

    val authService = new TwitterAuthServiceImpl(client, executionContext)

    val bridgeOptions = BridgeOptions()
      .addOutboundPermitted(PermittedOptions().setAddress(SOCKJS_ADDRESS))
    val port = config.getInteger("app.port", 8080)
    val updateInterval = config.getLong("app.update.interval", 5000L)

    val sockJSHandler = SockJSHandler.create(vertx).bridge(bridgeOptions)

    router.route("/eventbus/*").handler(sockJSHandler)
    router.route("/api/hashtag/:q").handler(apiSearchByHashTag)
    router.route().handler(StaticHandler.create())

    // Get request token from the configuration file.
    val rtkOpt = for {
      cKey <- Option(config.getString("app.consumerKey"))
      cSecret <- Option(config.getString("app.consumerSecret"))
    } yield RequestToken(cKey, cSecret)

    rtkOpt match {
      case Some(rtk) =>
        authService.auth(rtk)
          .map(token => {
            searchService = new TweetSearchServiceImpl(client, token.accessToken, executionContext)
            vertx.setPeriodic(updateInterval, _ => pushTweets()) // Start timer for fetching tweets.
          })
          .flatMap(_ => createServer(router, port))
          .map(_ => ())
      case None => Future.failed(new Exception("No consumer key found in configuration"))
    }
  }

  private def apiSearchByHashTag(context: RoutingContext): Unit = context.request().getParam("q") match {
    case Some(tag) =>
      hashTag = tag // This is thread-safe in Vert.x thread model.
      pushTweets()
    case None => context.fail(400) // Bad request.
  }

  private def pushTweets(): Unit = searchService.search(hashTag) onComplete {
    case Success(tweet) =>
      vertx.eventBus().publish(SOCKJS_ADDRESS, tweet.encode())
    case Failure(ex) => ex.printStackTrace()
  }

  // Helper functions.

  private def createServer(router: Router, port: Int) = {
    vertx.createHttpServer
      .requestHandler(router.accept _)
      .listenFuture(port)
  }
}
