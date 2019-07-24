package org.jrosenkranz.opts.options

import org.jrosenkranz.opts.constants.OptTypes

/**
  * A special form of [[DefaultOpt]] where no input is needed
  *
  * @param key the key for this option
  * @param message the message for this option
  * @param abrKey the abbreviation key for this option
  */
@SerialVersionUID(-5915788503867053831L)
class FlagOpt(key: String, message: String, abrKey: Option[String])
  extends DefaultOpt[Boolean](key,false,message,abrKey, _ => true, OptTypes.UNKNOWN) {

  override def toString: String = {
    s"--$key$abrFormatted"+ "\n" +
      s"$messageFormatted" + "\n"
  }
}

object FlagOpt {
  /**
    * create a [[FlagOpt]]
    *
    * @param key the [[Opt]] key
    * @param message the [[Opt]] message
    * @param abrKey the [[Opt]] abbreviation key
    * @return a new [[FlagOpt]]
    */
  def apply(key: String, message: String = "",abrKey: String = ""): DefaultOpt[Any] = {
    new FlagOpt(key, message, if (abrKey == "") None else Some(abrKey)).asInstanceOf[DefaultOpt[Any]]
  }
}
