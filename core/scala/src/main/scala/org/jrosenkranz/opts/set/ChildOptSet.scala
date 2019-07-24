package org.jrosenkranz.opts.set

import org.jrosenkranz.opts.options.{CommandOpt, Opt}

/**
  * This is a special form of [[OptSet]] that is used from within a [[CommandOpt]]
  *
  * @param childOpts a sequence of [[Opt]]
  */
@SerialVersionUID(-7914198506619297587L)
class ChildOptSet(childOpts: Opt[Any]*) extends OptSet(childOpts :_*) {
  override def toString(): String = {
    val required = {
      if (!childOpts.exists(_.isRequired))
        " "
      else
        " " + childOpts.filter(_.isRequired).map(_.key).map(key => s"--$key <${key.toUpperCase}>").mkString(" ").trim + " "
    }

    childOpts.toArray.filter(!_.isCmd).sortBy(!_.isRequired).map("\t" + _.toString).mkString("") + "\n" +
    childOpts.toArray.filter(_.isCmd).map(opt => {
      val childRequired = {
        if (!opt.asInstanceOf[CommandOpt].childOpts.exists(_.isRequired))
          " "
        else
          opt.asInstanceOf[CommandOpt].childOpts.filter(_.isRequired).map(_.key)
            .map(key => s"--$key <${key.toUpperCase}>")
            .mkString(" ")
            .trim + " "
      }
      s"\tCommand: ${opt.key}$required$childRequired [options]" + "\n\t" + opt.toString
    }).mkString("\n\n")
  }

  override def +(elem: Opt[Any]): ChildOptSet = ChildOptSet(set.+(elem).toSeq :_*)

  override def -(elem: Opt[Any]): ChildOptSet = ChildOptSet(set.-(elem).toSeq :_*)
}

object ChildOptSet {
  /**
    * Create a [[ChildOptSet]] given a variable sequence of [[Opt]]
    *
    * @param childOpts a sequence of [[Opt]]
    * @return a new [[ChildOptSet]]
    */
  def apply(childOpts: Opt[Any]*): ChildOptSet = new ChildOptSet(childOpts :_*)
}

