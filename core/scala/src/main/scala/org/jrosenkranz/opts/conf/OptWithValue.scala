package org.jrosenkranz.opts.conf

import org.jrosenkranz.opts.options.Opt

/**
  * A mutable class with special form of value where an [[Opt]] is stored with the value. This is used widely in
  * [[ArgParser]]
  *
  * @param opt the [[Opt]]
  * @param optValue the value associated with the [[Opt]]
  * @tparam T the type of value
  */
@SerialVersionUID(6363378258405371705L)
class OptWithValue[T](opt:Opt[Any],optValue:Option[T] = None) extends Serializable {
  private var mutableValue = optValue
  def update(value: Option[T]): Unit = {
    mutableValue = value
  }
  def get(): Option[T] = mutableValue
  def apply(): T = mutableValue.get
}
