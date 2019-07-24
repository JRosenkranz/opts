package org.jrosenkranz.opts.test

import org.jrosenkranz.opts.options.{CommandOpt, DefaultOpt, FlagOpt, RequiredOpt}
import org.jrosenkranz.opts.set.{ChildOptSet, OptSet}
import org.scalatest.FunSuite
import org.scalatest.Matchers._

class OptsSpec extends FunSuite {

  val opts = OptSet(
    RequiredOpt("key1",abrKey = "k1"),
    DefaultOpt("key2","localhost"),
    DefaultOpt("key3",8080,parseOp = _.toInt),
    CommandOpt("cmd1","",ChildOptSet(
      RequiredOpt("cmd1_key1"),
      FlagOpt("flag1","")
    )),
    CommandOpt("cmd2","",ChildOptSet(
      DefaultOpt("cmd2_key1",Array(1,2,3),parseOp = s => s.split(",").map(_.toInt)),
      RequiredOpt("cmd2_key2")
    ))
  )

  val defaultOpts = OptSet(
    DefaultOpt("key1","val1"),
    DefaultOpt("key2","val2")
  )

  val flagOpts = OptSet(
    FlagOpt("flag1")
  )

  def testPasses(optSet: OptSet,args: String*)(expected: Map[String,Any])(cmd: Option[Array[String]] = None): Unit = {
    optSet.parse(args.toArray) match {
      case Some(options) => {
        if (cmd.isDefined)
          options.commands.get should be (cmd.get)
        else {
          options.commands.isDefined should be (cmd.isDefined)
        }
        expected.foreach(entry => {
          entry._2 should be (options.as[Any](entry._1))
        })
      }
      case None => fail
    }
  }

  def testFails(optSet: OptSet,args: String*): Unit = {
    optSet.parse(args.toArray) match {
      case Some(_) => fail
      case None =>
    }
  }

  test("test all parameters given with command 1") {
    testPasses(
      opts,
      "cmd1","--key1","val1","--key2","val2","--key3","3","--cmd1_key1","cmd1_val1","--flag1"
    )(
      Map(
        "key1" -> "val1",
        "key2" -> "val2",
        "key3" -> 3,
        "cmd1_key1" -> "cmd1_val1",
        "flag1" -> true
      )
    )(
      Some(Array("cmd1"))
    )
  }

  test("test only required given with no commands") {
    testPasses(
      opts,
      "--key1","val1"
    )(
      Map(
        "key1" -> "val1",
        "key2" -> "localhost",
        "key3" -> 8080
      )
    )(
      None
    )
  }

  test("test only required given with command 1") {
    testPasses(
      opts,
      "cmd1","--key1","val1","--key2", "val2", "--key3", "8080","--cmd1_key1","cmd1_val1"
    )(
      Map(
        "key1" -> "val1",
        "key2" -> "val2",
        "key3" -> 8080,
        "cmd1_key1" -> "cmd1_val1",
        "flag1" -> false
      )
    )(
      Some(Array("cmd1"))
    )
  }

  test("not all required given from OptSet with command 2") {
    testFails(opts,"cmd2","--cmd2_key2","val2")
  }

  test("not all required given from ChildOptSet with command 2") {
    testFails(opts,"cmd2","--key1","val1")
  }

  test("not all required given with no commands") {
    testFails(opts,"--cmd1_key1","val1","--cmd2_key2","val2")
  }

  test("no arguments given when all options are default type (not required)") {
    testPasses(
      defaultOpts
    )(
      Map(
        "key1" -> "val1",
        "key2" -> "val2"
      )
    )(
      None
    )
  }

  test("last argument is an InputOpt but not a FlagOpt - argument requires user input but no user input is given") {
    testFails(
      opts,"--key1"
    )
  }

  test("last argument is a FlagOpt - argument requires no user input") {
    testPasses(
      flagOpts,"--flag1"
    )(
      Map(
        "flag1" -> true
      )
    )(
      None
    )
  }
}
