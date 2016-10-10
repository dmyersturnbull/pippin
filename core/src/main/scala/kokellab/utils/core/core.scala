package kokellab.utils

import java.io.File

import com.typesafe.config.{Config, ConfigException, ConfigFactory, ConfigParseOptions}
import com.typesafe.scalalogging.LazyLogging

package object core extends LazyLogging {

	def parseConfig(path: String): Config = parseConfig(new File(path))
	def parseConfig(path: File): Config =
		ConfigFactory.parseFile(path, ConfigParseOptions.defaults().setAllowMissing(false))

	def configOptionString(key: String)(implicit config: Config): Option[String] = configOption(key, config.getString)
	def configOptionInt(key: String)(implicit config: Config): Option[Int] = configOption(key, config.getInt)
	def configOptionBoolean(key: String)(implicit config: Config): Option[Boolean] = configOption(key, config.getBoolean)
	private def configOption[V](key: String, extractor: String => V): Option[V] = try {
		Some(extractor(key))
	} catch {
		case e: ConfigException.Missing => None
	}

	/** Logs an error message for any exception, then rethrows. */
	def withLoggedError[T](errorMessage: String, fn: () => T): T = withLoggedError(fn, errorMessage) // helpful for long functions

	/** Logs an error message for any exception, then rethrows. */
	def withLoggedError[T](fn: () => T, errorMessage: String): T = try {
			fn()
		} catch {
			case e: Exception =>
				logger.error(errorMessage)
				throw e
		}

	/**
		* Looks up all of the given keys in the map.
		* Throws an InvalidDataFormatException if conflicting values are found.
		* If the value is not found, returns None. If it is found (and is unique), returns the value.
		*/
	def uniqueLookupOption[A, B](keys: Set[A], map: Map[A, B]): Option[B] = {
		val newSet: Set[B] = keys.foldLeft(Set.empty[B])((current, next) => {
			if (map contains next) current + map(next) else current
		})
		if (newSet.size < 2) newSet.headOption
		else throw new IllegalArgumentException(s"There were ${newSet.size} conflicting values for keys {${keys.mkString(",")}}")
	}
	def uniqueLookup[A, B](keys: Set[A], map: Map[A, B]): B =
		uniqueLookupOption(keys, map) getOrElse (throw new IllegalArgumentException("No value for keys {${keys.mkString(\",\")}}"))

}
