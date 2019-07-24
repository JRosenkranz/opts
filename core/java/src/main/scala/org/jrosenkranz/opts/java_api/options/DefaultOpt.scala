package org.jrosenkranz.opts.java_api.options

import org.jrosenkranz.opts.constants.OptTypes
import org.jrosenkranz.opts.constants.OptTypes.OptTypes
import org.jrosenkranz.opts.options.{DefaultOpt => JDefaultOpt}
import org.jrosenkranz.opts.constants.OptTypes

class DefaultOpt[T](key: String,default: T,message: String,abrKey: Option[String], parseOp: (String) => T, optType: OptTypes)
  extends JDefaultOpt[T](key,default,message,abrKey,parseOp,optType){

  def message(msg: String): DefaultOpt[T] = {
    new DefaultOpt[T](key,default,msg,abrKey,parseOp,optType)
  }

  def abrev(abr: String): DefaultOpt[T] = {
    new DefaultOpt[T](key,default,message,Some(abr),parseOp,optType)
  }

  def parseOp(f: java.util.function.Function[String,T]): DefaultOpt[T] = {
    new DefaultOpt[T](key,default,message,abrKey,s => f.apply(s),optType)
  }

  override def toString: String = super.toString
}

object DefaultOpt {
  def apply[T](key: String, default: T): DefaultOpt[T] = {
    new DefaultOpt(key, default, "", None, s => s.asInstanceOf[T], OptTypes.UNKNOWN)
  }
}
