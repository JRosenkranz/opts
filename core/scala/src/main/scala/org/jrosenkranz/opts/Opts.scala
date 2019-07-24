package org.jrosenkranz.opts

import org.jrosenkranz.opts.set.OptSet

/**
  * This is the main class that holds all of the realized options and any commands that may be associated with the
  * options after parsing arguments
  *
  * @param parameterMap map from opt key to a value
  * @param cmd an array of commands
  */
@SerialVersionUID(-1809250462238591840L)
class Opts(parameterMap: Map[String,Any],cmd: Array[String]) extends Map[String,Any] with Serializable {

  /**
    * array of commands that were parsed out of the arguments
    */
  val commands: Option[Array[String]] = if (cmd.isEmpty) None else Some(cmd)

  /**
    * get a value for a given key
    *
    * @param key the option key
    * @tparam T the type to receive
    * @return a value for the given key
    */
  def as[T](key: String): T = parameterMap(key).asInstanceOf[T]

  /**
    * gets a value as a string given a key
    *
    * @param key the option key
    * @return a value for the given key as a String
    */
  override def apply(key: String): String = parameterMap(key).toString

  /**
    * gets an optional value for a given key
    *
    * @param key the option key
    * @tparam T the type to receive
    * @return some value for the given key if the key exists, otherwise none
    */
  def getAs[T](key: String): Option[T] = parameterMap.get(key).asInstanceOf[Option[T]]

  override def get(key: String): Option[Any] = parameterMap.get(key)

  override def iterator: Iterator[(String, Any)] = parameterMap.iterator

  override def -(key: String): Map[String, Any] = parameterMap.-(key)

  override def +[B1 >: Any](kv: (String, B1)): Map[String, B1] = parameterMap.+(kv)
}

object Opts {

  /**
    * create an opts with a fail fast strategy. Fail fast as in, if a parsing error occurs, the program will print
    * the fail message for options and exit
    *
    * @param parameterSet the provided optSet schema
    * @param args arguments given in command line
    * @param cmdRequired denotes if a command is required, if true command is required, otherwise a command is not
    *                    required
    * @return an [[Opts]] object
    */
  def apply(parameterSet: OptSet, args: Array[String],cmdRequired: Boolean = false): Opts = {
    val res = parameterSet.parseWithResponse(args,cmdRequired)

    if (res._1.isEmpty) {
      println(res._2)
      System.exit(0)
    }

    res._1.get
  }
}
