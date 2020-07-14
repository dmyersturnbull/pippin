package pippin.grammars

package object params {

	val paramPattern = """\$\{?(?:\.{3})?[A-Za-z0-9_]*(?:\[[\$A-Za-z0-9_]+\])?\}?""".r

}
