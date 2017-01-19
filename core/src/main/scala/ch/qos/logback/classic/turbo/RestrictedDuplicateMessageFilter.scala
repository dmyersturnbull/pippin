package ch.qos.logback.classic.turbo

import ch.qos.logback.classic.{Level, Logger}
import ch.qos.logback.core.spi.FilterReply
import org.slf4j.Marker

import scala.annotation.switch
import scala.beans.BeanProperty

/**
  * A Logback log filter that prevents messages with the same format string from being logged
  *
  * @param packagesAlwaysAllowed Comma-separated list of fully qualified package names: Permit any number of messages with the same format if they're from any package descended from any of these packages
  * @param nRepetitionsAllowed Permit at most this number of messages with the same format
  * @param cacheSize Store up to this many message formats for comparison; if more message formats are seen than this number, new messages with formats seen previously may be logged despite exceeding the count limit
  * @param fixedLogLevel Use this SLF4J log level to state that further messages will be ignored; if < 0, the same level as the log message ignored will be used
  */
class RestrictedDuplicateMessageFilter(@BeanProperty var packagesAlwaysAllowed: String, @BeanProperty var nRepetitionsAllowed: Int = 1, @BeanProperty var cacheSize: Int = 200, @BeanProperty var fixedLogLevel: Bool=-1) extends TurboFilter {

	private var msgCache: LRUMessageCache = null

	def this() = this(null, 1)

	def decide(marker: Marker, logger: Logger, level: Level, format: String, params: Array[AnyRef], throwable: Throwable): FilterReply = {
		if (packagesAlwaysAllowed != null && (packagesAlwaysAllowed split ',' exists (s => logger.getName startsWith s))) FilterReply.NEUTRAL
		else msgCache.getMessageCountAndThenIncrement(format) match {
			case x if x < nRepetitionsAllowed => FilterReply.NEUTRAL
			case x if x == nRepetitionsAllowed =>
				val slf4jl = if (fixedLogLevel < 0) slf4jLevel(level) else fixedLogLevel
				logger.log(marker, Logger.FQCN, slf4jl, s"...ignoring any further messages with the format $format.", null, throwable)
				FilterReply.DENY
			case _ => FilterReply.DENY
		}
	}

	override def start() {
		msgCache = new LRUMessageCache(cacheSize)
		super.start()
	}

	override def stop() {
		msgCache.clear()
		msgCache = null
		super.stop()
	}

	/**
	  * Defined in org.slf4j.spi.LocationAwareLogger.
	  */
	private def slf4jLevel(level: Level): Int = Map(
		Level.TRACE -> 0,
		Level.DEBUG -> 10,
		Level.INFO -> 20,
		Level.WARN -> 30,
		Level.ERROR -> 40
	)(level)

}
