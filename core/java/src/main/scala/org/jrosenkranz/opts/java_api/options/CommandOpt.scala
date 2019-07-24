package org.jrosenkranz.opts.java_api.options

import org.jrosenkranz.opts.set.ChildOptSet
import org.jrosenkranz.opts.options.{Opt, CommandOpt => JCommandOpt}

class CommandOpt(key: String, message: String, childOptSet: ChildOptSet) extends JCommandOpt(key,message,childOptSet) {
  def message(msg: String): CommandOpt = {
    new CommandOpt(key,msg,childOptSet)
  }

  def addChild(opt:Opt[Any]): CommandOpt = {
    new CommandOpt(key,message,ChildOptSet(childOptSet.+(opt).toSeq :_*))
  }

  override def toString: String = super.toString
}

object CommandOpt {
  def apply(key: String): CommandOpt = new CommandOpt(key,"",ChildOptSet())
}


