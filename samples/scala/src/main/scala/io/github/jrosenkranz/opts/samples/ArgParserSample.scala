package io.github.jrosenkranz.opts.samples

import io.github.jrosenkranz.opts.conf.ArgParser

object ArgParserSample {
  val opts = new ArgParser {
    val update = new commandOpt("update","update the system") {
      val host = requiredOpt("host","kafka host")
      val port = defaultOpt("port",2181,"kafka port")
      val help = flagOpt("help","this is help","h")
    }

    val delete = new commandOpt("delete","delete the system")
    val empty = new commandOpt("empty","this is an empty command")

    val topics = defaultOpt("topics",Array("sample"), "kafka topics comma separated",parseOp = _.split(","))
    val inputPath = requiredOpt[String]("inputPath","path to input")
    val delimiter = defaultOpt("delimiter",",","kafka message delimiter", "d")
  }

  def main(args: Array[String]): Unit = {
    opts.init(args)

    println(s"topics: ${opts.topics().mkString(",")}")
    println(s"input path: ${opts.inputPath()}")
    println(s"delimiter: ${opts.delimiter()}")

    opts.cmd match {
      case Some("update") => {
        println("running update command")
        println(s"host: ${opts.update.host()}")
        println(s"port: ${opts.update.port()}")
        println(s"help: ${opts.update.help()}")
      }

      case Some("delete") => {
        println("running delete command")
        //println(s"name: ${conf.delete.name()}")
      }

      case Some("empty") =>
        println("this is an empty command")

      case None => println("no command was issued")
    }

  }
}
