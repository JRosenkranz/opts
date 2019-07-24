package org.jrosenkranz.opts.samples

import org.jrosenkranz.opts.Opts
import org.jrosenkranz.opts.options.{CommandOpt, DefaultOpt, FlagOpt, RequiredOpt}
import org.jrosenkranz.opts.set.{ChildOptSet, OptSet}

object OptsSample {
  val os = OptSet(
    DefaultOpt("topics",List("sample"), "kafka topics comma separated","t",_.split(",").toList),
    DefaultOpt("inputPath","./","path to input"),
    DefaultOpt("delimiter",",","kafka message delimiter"),
    FlagOpt("help","this will display all commands as a list"),
    CommandOpt("update","update the system",ChildOptSet(
      RequiredOpt("host","kafka host"),
      DefaultOpt("port",2181,"kafka port"),
      FlagOpt("help","this will display the update help")
    )),
    CommandOpt("delete","delete the system",ChildOptSet(
      RequiredOpt("name","item to delete")
    ))
  )

  def main(args: Array[String]): Unit = {

    os.parse(args) match {
      //parsing passed
      case Some(options) => {

        //check what to do depending on the commands given
        options.commands match {

          case Some(Array("update")) => handleUpdate(options)

          case Some(Array("delete")) => handleDelete(options)

          case None => handleNoCommand(options)
        }

      }
      //parsing failed
      case None => println(os.toString())
    }
  }

  private def handleUpdate(options: Opts): Unit = {
    println(options)
  }

  private def handleDelete(options: Opts): Unit = {
    println(options)
  }

  private def handleNoCommand(options: Opts): Unit = {
    //if help is true, print out the available commands
    if (options.as[Boolean]("help")) {
      println(os.filter(_.isCmd).map(opt => s"Command: ${opt.key}   ${opt.message}").mkString("\n"))
    } else {
      println(os)
    }
  }
}
