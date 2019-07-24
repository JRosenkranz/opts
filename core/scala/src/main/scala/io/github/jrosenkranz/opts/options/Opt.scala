package io.github.jrosenkranz.opts.options

/**
  * The most basic form of option that all other options derive from. A sequence of options are what make up an option
  * schema or [[io.github.jrosenkranz.opts.set.OptSet]]
  *
  * @param key the key for this option
  * @param message the message for this option
  * @param abrKey the abbreviation key for this option
  * @tparam T the type of value for this option
  */
abstract case class Opt[T](key: String, message: String, abrKey: Option[String] = None) extends Serializable {
  /**
    * denotes if an option has input associated with it
    */
  val hasInput: Boolean

  /**
    * denotes whether this option is required
    */
  val isRequired: Boolean

  /**
    * denotes whether this option is a command
    */
  val isCmd: Boolean = false

  /**
    * extract the value from this opt given the value as a String
    *
    * @param value the value as a String
    * @return a new value as the type of this opt
    */
  def extractValue(value: String): T

  /**
    * Opt equality is based on the [[Opt]] key
    * @param obj some object
    * @return true if the object is an [[Opt]] and other's key is equal to this key, otherwise return false
    */
  override def equals(obj: scala.Any): Boolean = {
    key.equals(obj)
  }

  protected val messageFormatted: String = {
    s"${
      if (message.nonEmpty && message.charAt(0) == '\n')
        message.stripPrefix("\n").split("\n").map(s => s"\t\t$s").mkString("\n")
      else
        s"\t\t$message"
    }"
  }

  protected val abrFormatted: String = {
    s"${
      if (abrKey.isDefined)
        s", -${abrKey.get}"
      else
        ""
    }"
  }
}
