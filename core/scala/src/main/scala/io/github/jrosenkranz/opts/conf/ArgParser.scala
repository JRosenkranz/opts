package io.github.jrosenkranz.opts.conf

import io.github.jrosenkranz.opts.Opts
import io.github.jrosenkranz.opts.constants.OptTypes
import io.github.jrosenkranz.opts.options.{CommandOpt, DefaultOpt, FlagOpt, RequiredOpt}
import io.github.jrosenkranz.opts.set.{ChildOptSet, OptSet}

import scala.collection.mutable
import scala.reflect.runtime.universe._

/**
  * A Mutable class that mimics [[Opts]] with val accessors
  *
  * Opts are given in the form of extension of this class as
  * {{{
  *   class MyParser extends ArgParser {
  *     val default1 = defaultOpt(...)
  *     val required1 = requiredOpt[String](...)
  *     val flag1 = flagOpt(...)
  *     val command1 = new CommandOpt(...) {
  *       val required2 = requiredOpt[Double](...)
  *       //more args go here
  *     }
  *     val command2 = new CommandOpt(...) {
  *       val default2 = defaultOpt(...)
  *       //more args go here
  *     }
  *     //more args go here
  *   }
  * }}}
  *
  * Once a new instantiation of this extended class is initialized, the following args can be called as
  *
  * {{{
  *   val options = new MyParser()
  *   options.init(args)
  *
  *   val def1 = options.default1() //this is a string
  *   val req2 = options.command1.required2() //this is a double
  * }}}
  */
@SerialVersionUID(-7112418510095266747L)
class ArgParser extends Serializable {
  private var valueMap: mutable.Map[String,OptWithValue[Any]] = mutable.Map.empty[String,OptWithValue[Any]]
  private var optSet: OptSet = OptSet()
  var cmd: Option[String] = None

  def init(args: Array[String],cmdRequired: Boolean = false): Unit = {
    val opts = Opts(optSet,args,cmdRequired)
    if (opts.commands.isDefined) {
      cmd = opts.commands.get.headOption
    }
    valueMap.foreach(kv => {
      kv._2.update(opts.get(kv._1))
    })
  }

  def parse(args: Array[String],cmdRequired: Boolean = false): Boolean = {
    val opts = optSet.parse(args,cmdRequired)
    opts match {
      case Some(o) => {
        if (o.commands.isDefined) {
          cmd = o.commands.get.headOption
        }
        valueMap.foreach(kv => {
          kv._2.update(o.get(kv._1))
        })
        true
      }
      case None => false
    }
  }

  override def toString: String = {
    optSet.toString()
  }


  protected def requiredOpt[T:TypeTag]
  (
    key: String,
    message: String = "",
    abrKey: String = "",
    parseOp: String => T = null,
    optType: OptTypes.Value = OptTypes.STRING
  ): OptWithValue[T] = {
    val opt = RequiredOpt(key,message,abrKey,parseOp,optType)
    optSet += opt
    valueMap += (key -> new OptWithValue[Any](opt))
    valueMap(key).asInstanceOf[OptWithValue[T]]
  }

  protected def defaultOpt[T:TypeTag]
  (
    key: String,
    default: T,
    message: String = "",
    abrKey: String = "",
    parseOp: String => T = null,
    optTypes: OptTypes.Value = OptTypes.UNKNOWN
  ): OptWithValue[T] = {
    val opt = DefaultOpt(key,default,message,abrKey,parseOp,optTypes)
    optSet += opt
    valueMap += (key -> new OptWithValue[Any](opt))
    valueMap(key).asInstanceOf[OptWithValue[T]]
  }

  protected def flagOpt(key: String, message: String, abrKey: String = ""): OptWithValue[Boolean] = {
    val opt = FlagOpt(key,message,abrKey)
    optSet += opt
    valueMap += (key -> new OptWithValue[Any](opt))
    valueMap(key).asInstanceOf[OptWithValue[Boolean]]
  }

  class commandOpt(key: String, message: String) extends Serializable {
    private var childOptSet: ChildOptSet = ChildOptSet()
    private var cmdOpt = CommandOpt(key,message)
    valueMap += (key -> new OptWithValue[Any](cmdOpt))
    optSet += cmdOpt

    protected def requiredOpt[T:TypeTag]
    (
      key: String,
      message: String = "",
      abrKey: String = "",
      parseOp: String => T = null,
      optType: OptTypes.Value = OptTypes.STRING
    ): OptWithValue[T] = {
      val opt = RequiredOpt(key,message,abrKey,parseOp,optType)
      childOptSet += opt
      optSet -= cmdOpt
      cmdOpt = CommandOpt(cmdOpt.key,cmdOpt.message,childOptSet)
      optSet += cmdOpt
      valueMap += (cmdOpt.key -> new OptWithValue[Any](cmdOpt))
      valueMap += (key -> new OptWithValue[Any](opt))
      valueMap(key).asInstanceOf[OptWithValue[T]]
    }

    protected def defaultOpt[T:TypeTag]
    (
      key: String,
      default: T,
      message: String = "",
      abrKey: String = "",
      parseOp: String => T = null,
      optTypes: OptTypes.Value = OptTypes.UNKNOWN
    ): OptWithValue[T] = {
      val opt = DefaultOpt(key,default,message,abrKey,parseOp,optTypes)
      childOptSet += opt
      optSet -= cmdOpt
      cmdOpt = CommandOpt(cmdOpt.key,cmdOpt.message,childOptSet)
      optSet += cmdOpt
      valueMap += (cmdOpt.key -> new OptWithValue[Any](cmdOpt))
      valueMap += (key -> new OptWithValue[Any](opt))
      valueMap(key).asInstanceOf[OptWithValue[T]]
    }

    protected def flagOpt(key: String, message: String, abrKey: String = ""): OptWithValue[Boolean] = {
      val opt = FlagOpt(key,message,abrKey)
      childOptSet += opt
      optSet -= cmdOpt
      cmdOpt = CommandOpt(cmdOpt.key,cmdOpt.message,childOptSet)
      optSet += cmdOpt
      valueMap += (cmdOpt.key -> new OptWithValue[Any](cmdOpt))
      valueMap += (key -> new OptWithValue[Any](opt))
      valueMap(key).asInstanceOf[OptWithValue[Boolean]]
    }
  }
}
