package kokellab.utils.core.exceptions

class ServiceFailedException(message: String, cause: Throwable = null, val serviceOutput: String = "") extends Exception(message, cause) {
	def this(message: String) = this(message, null)
	def this(cause: Throwable) = this(null, cause)
        def getServiceOutput: String = serviceOutput
}
