package io.github.jrosenkranz.opts.test

import io.github.jrosenkranz.opts.conf.ArgParser
import org.scalatest.FunSuite
import org.scalatest.Matchers._

class ArgParserSpec extends FunSuite {
  class Args extends ArgParser {
    val cmd3 = new commandOpt("cmd3","this is command 3") {
      val req31 = requiredOpt[String]("req31","this is command 3 - req 1")
      val def31 = defaultOpt("def31","default","this is command 3 - def 1")
    }
    val def1 = defaultOpt("def1",1,"this is def 1")
    val req1 = requiredOpt[String]("req1","this is req 1")
    val cmd1 = new commandOpt("cmd1","this is command 1") {
      val req11 = requiredOpt[String]("req11","this is command 1 - req 1")
    }
    val cmd2 = new commandOpt("cmd2","this is command 2")
  }



  test("test all parameters given with command 1") {
    val opts = new Args()
    val succeeded = opts.parse(Array("cmd1","--req11","11","--req1","1","--def1","5"),true)
    succeeded should be (true)
    opts.cmd should be (Some("cmd1"))
    opts.def1() should be (5)
    opts.req1() should be ("1")
    opts.cmd1.req11() should be ("11")
  }

  test("test parameters omitting default parameters") {
    val opts = new Args()
    val succeeded = opts.parse(Array("cmd1","--req11","11","--req1","1"),true)
    succeeded should be (true)
    opts.cmd should be (Some("cmd1"))
    opts.def1() should be (1)
    opts.req1() should be ("1")
    opts.cmd1.req11() should be ("11")
  }

  test("test not adding command's child required opt") {
    val opts = new Args()
    val succeeded = opts.parse(Array("cmd3","--req1","1"),true)
    succeeded should be (false)
  }

  test("test adding command's child required opt but not giving base req opt") {
    val opts = new Args()
    val succeeded = opts.parse(Array("cmd3","--req31","31"),true)
    succeeded should be (false)
  }

  test("test giving valid command with required opts") {
    val opts = new Args()
    val succeeded = opts.parse(Array("cmd3","--req31","31","--req1","1"),true)
    succeeded should be (true)
    opts.cmd should be (Some("cmd3"))
    opts.def1() should be (1)
    opts.req1() should be ("1")
    opts.cmd3.req31() should be ("31")
    opts.cmd3.def31() should be ("default")
  }

  test("test empty command without based required opt") {
    val opts = new Args()
    val succeeded = opts.parse(Array("cmd2"),true)
    succeeded should be (false)
  }

  test("test empty command passes") {
    val opts = new Args()
    val succeeded = opts.parse(Array("cmd2","--req1","1"),true)
    succeeded should be (true)
    opts.cmd should be (Some("cmd2"))
    opts.def1() should be (1)
    opts.req1() should be ("1")
  }

  test("test command not required without passing command") {
    val opts = new Args()
    val succeeded = opts.parse(Array("--req1","1"),false)
    succeeded should be (true)
    opts.cmd should be (None)
    opts.def1() should be (1)
    opts.req1() should be ("1")
  }

  test("test command not required without passing base required opt") {
    val opts = new Args()
    val succeeded = opts.parse(Array(),false)
    succeeded should be (false)
  }

  test("test command not required with passing command") {
    val opts = new Args()
    val succeeded = opts.parse(Array("cmd1","--req11","11","--req1","1"),false)
    succeeded should be (true)
    opts.cmd should be (Some("cmd1"))
    opts.def1() should be (1)
    opts.req1() should be ("1")
    opts.cmd1.req11() should be ("11")
  }

  test("test command not required with passing command without adding nested required opt") {
    val opts = new Args()
    val succeeded = opts.parse(Array("cmd1","--req1","1"),false)
    succeeded should be (false)
  }
}
