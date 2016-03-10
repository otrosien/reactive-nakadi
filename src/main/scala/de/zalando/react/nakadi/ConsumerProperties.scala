package de.zalando.react.nakadi

import de.zalando.react.nakadi.commit.handlers.BaseHandler

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration


object ConsumerProperties {

  def apply(
    server: String,
    securedConnection: Boolean,
    tokenProvider: () => String,
    topic: String,
    groupId: String
  ): ConsumerProperties = {
    if (securedConnection) { new ConsumerProperties(
      server = server,
      securedConnection = securedConnection,
      tokenProvider = tokenProvider,
      topic = topic,
      groupId = groupId,
      port = 443,
      urlSchema = "https://"
    )} else { new ConsumerProperties(
      server = server,
      securedConnection = securedConnection,
      tokenProvider = tokenProvider,
      topic = topic,
      groupId = groupId,
      port = 80,
      urlSchema = "http://"
    )}
  }
}

case class ConsumerProperties(
  server: String,
  securedConnection: Boolean,
  tokenProvider: () => String,
  topic: String,
  groupId: String,
  port: Int = 80,
  offset: String = "earliest",
  commitInterval: Option[FiniteDuration] = None,
  consumerTimeoutSec: FiniteDuration = 5.seconds,
  batchLimit: Int = 0,
  batchFlushTimeoutInSeconds: FiniteDuration = 0.seconds,
  streamLimit: Int = 0,
  streamTimeoutInSeconds: FiniteDuration = 0.seconds,
  streamKeepAliveLimit: Int = 0,
  pollParallelism: Int = 0,
  autoReconnect: Boolean = false,
  sslVerify: Boolean = true,
  urlSchema: String = "https://",
  commitHandler: Option[BaseHandler] = None
) {

  /**
    * Use custom interval for auto-commit or commit flushing on manual commit.
    */
  def commitInterval(time: FiniteDuration): ConsumerProperties =
    this.copy(commitInterval = Option(time))

  /**
    * Consumer Timeout
    * Throw a timeout exception to the consumer if no message is available for consumption after the specified interval
    */
  def consumerTimeoutSec(timeInSec: FiniteDuration): ConsumerProperties =
    this.copy(consumerTimeoutSec = timeInSec)

  def readFromEndOfStream(): ConsumerProperties =
    this.copy(offset = "latest")

  def withPort(port: Int): ConsumerProperties =
    this.copy(port = port)

  def withUrlSchema(urlSchema: String): ConsumerProperties = {
    if (!Seq("http://", "https://").contains(urlSchema))
      throw new IllegalArgumentException("Must pass in valid schema of http:// or https://")
    else
      this.copy(urlSchema = urlSchema)
  }

}
