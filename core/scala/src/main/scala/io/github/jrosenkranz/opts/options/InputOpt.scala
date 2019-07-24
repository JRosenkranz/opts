package io.github.jrosenkranz.opts.options

/**
  * Special form of opt that has some input associate with it. In the scope of this project, that means some option
  * where if specified as an arg, would have a prefix of -- or -
  *
  * @param key the key for this option
  * @param message the message for this option
  * @param abrKey the abbreviation key for this option
  * @tparam T the type of value for this option
  */
abstract class InputOpt[T](key: String,message: String, abrKey: Option[String]) extends Opt[T](key,message, abrKey) {
  override val hasInput: Boolean = true
}
