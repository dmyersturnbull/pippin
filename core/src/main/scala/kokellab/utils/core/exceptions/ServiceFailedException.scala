package kokellab.utils.core.exceptions

class ServiceFailedException(message: String, cause: Throwable = null, serviceOutput: String = "") extends Exception(message, cause) {
	def this(message: String) = this(message, null)
	def this(cause: Throwable) = this(null, cause)
}
