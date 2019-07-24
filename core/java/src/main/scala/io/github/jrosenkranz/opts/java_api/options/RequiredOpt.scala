package io.github.jrosenkranz.opts.java_api.options

import io.github.jrosenkranz.opts.constants.OptTypes
import io.github.jrosenkranz.opts.options.{RequiredOpt => JRequiredOpt}


class RequiredOpt[T](key: String, message: String, abrKey: Option[String], parseOp: (String) => T,optType: OptTypes.Value = OptTypes.STRING)
  extends JRequiredOpt[T](key,message,abrKey,parseOp,optType) {

  def abrev(abrKey: String): RequiredOpt[T] = {
    new RequiredOpt[T](key,message,Some(abrKey),parseOp,optType)
  }

  def parseOp(f: java.util.function.Function[String,T]): RequiredOpt[T] = {
    new RequiredOpt[T](key,message,abrKey,s => f.apply(s),optType)
  }

  def message(msg: String): RequiredOpt[T] = {
    new RequiredOpt[T](key,msg,abrKey,parseOp,optType)
  }

  override def toString: String = super.toString
}

object RequiredOpt {
  def apply[T](key: String): RequiredOpt[T] = new RequiredOpt(key, "", None,s => s.asInstanceOf[T],OptTypes.STRING)
}
