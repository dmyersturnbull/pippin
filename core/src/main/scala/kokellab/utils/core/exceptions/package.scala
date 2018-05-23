package kokellab.utils.core

package object exceptions {

	class InvalidDataFormatException(message: String, cause: Throwable = null) extends Exception(message, cause)

	class MultipleElementsException(message: String, cause: Throwable = null) extends Exception(message, cause)

	class ServiceFailedException(message: String, cause: Throwable = null, serviceOutput: Option[String] = None) extends Exception(message, cause)


}
