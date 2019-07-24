package io.github.jrosenkranz.opts.options

import io.github.jrosenkranz.opts.set.{ChildOptSet, OptSet}

/**
  * A special form of [[Opt]] that contains a [[ChildOptSet]] which are only to be associated with the final result if
  * this command is specified in the arguments given. Unlike other [[Opt]] this option will always be at the front of
  * the command line arguments and will not be prefixed with a - or --
  *
  * @param key the key for this option
  * @param message the message for this option
  * @param optSet this commands child [[Opt]]
  */
@SerialVersionUID(-2551068568819956533L)
class CommandOpt(key: String, message: String, optSet: ChildOptSet) extends Opt[Any](key,message){
  override val hasInput: Boolean = false
  override val isRequired: Boolean = false
  override val isCmd: Boolean = true
  override def extractValue(value: String): Any = value

  /**
    * the child [[Opt]] associated with this command option
    */
  val childOpts: OptSet = optSet

  override def toString: String = {
    s"$message" + "\n" +
      optSet.toString()
  }
}

object CommandOpt {
  /**
    * create a [[CommandOpt]]
    *
    * @param key the [[Opt]] key
    * @param message the [[Opt]] message
    * @param optSet the commands child [[Opt]]
    * @return a new [[CommandOpt]]
    */
  def apply(key: String, message: String = "", optSet: ChildOptSet = ChildOptSet()): CommandOpt = new CommandOpt(key, message, optSet)
}
