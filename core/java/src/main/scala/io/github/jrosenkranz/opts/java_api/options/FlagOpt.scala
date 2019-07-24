package io.github.jrosenkranz.opts.java_api.options

import io.github.jrosenkranz.opts.options.{FlagOpt => JFlagOpt}


class FlagOpt(key: String, message: String, abrKey: Option[String]) extends JFlagOpt(key,message,abrKey) {
  def abrev(abrKey: String): FlagOpt = new FlagOpt(key,message,Some(abrKey))

  def message(msg: String): FlagOpt = new FlagOpt(key,msg,abrKey)

  override def toString: String = super.toString
}

object FlagOpt {
  def apply(key: String): FlagOpt = new FlagOpt(key,"", None)
}
