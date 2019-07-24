package io.github.jrosenkranz.opts.options

import io.github.jrosenkranz.opts.constants.OptTypes

import scala.reflect.runtime.universe._

/**
  * A special form of [[Opt]] where this option is required as part of the command line arguments
  *
  * @param key the key for this option
  * @param message the message for this option
  * @param abrKey the abbreviation key for this option
  * @param optType the type of this opt to be specified on failure message
  * @param parseOp parse operation
  * @tparam T the type of value for this option
  */
@SerialVersionUID(-2291017867471166482L)
class RequiredOpt[T]
    (
      key: String,
      message: String = "",
      abrKey: Option[String],
      parseOp: String => T,
      optType: OptTypes.Value = OptTypes.STRING
    ) extends InputOpt[T](key,message,abrKey) {

  override def extractValue(value: String): T = parseOp(value)

  override def toString: String = {
    s"--$key$abrFormatted <${OptTypes.getName(optType)}> | required" + "\n" +
      s"$messageFormatted" + "\n"
  }

  override val isRequired: Boolean = true
}

object RequiredOpt {
  /**
    * create a [[RequiredOpt]]
    *
    * @param key the [[Opt]] key
    * @param message the [[Opt]] message
    * @param abrKey the [[Opt]] abbreviation key
    * @param parseOp parse operation
    * @param optType the type of this opt to be specified on failure message
    * @tparam T the type of value for this option
    * @return a new [[RequiredOpt]]
    */
  def apply[T: TypeTag](key: String, message: String = "",abrKey: String = "",parseOp: (String) => T = null, optType: OptTypes.Value = OptTypes.STRING): RequiredOpt[Any] = {
    val opt = typeOf[T] match {
      case t if t =:= typeOf[String] => OptTypes.STRING
      case t if t =:= typeOf[Int] => OptTypes.INTEGER
      case t if t =:= typeOf[Long] => OptTypes.LONG
      case t if t =:= typeOf[Double] => OptTypes.DOUBLE
      case t if t =:= typeOf[Array[String]] => OptTypes.ARRAY
      case t if t =:= typeOf[List[String]] => OptTypes.ARRAY
      case t if t =:= typeOf[Seq[String]] => OptTypes.ARRAY
      case t if t =:= typeOf[Array[Long]] => OptTypes.ARRAY
      case t if t =:= typeOf[List[Long]] => OptTypes.ARRAY
      case t if t =:= typeOf[Seq[Long]] => OptTypes.ARRAY
      case t if t =:= typeOf[Array[Int]] => OptTypes.ARRAY
      case t if t =:= typeOf[List[Int]] => OptTypes.ARRAY
      case t if t =:= typeOf[Seq[Int]] => OptTypes.ARRAY
      case t if t =:= typeOf[Array[Double]] => OptTypes.ARRAY
      case t if t =:= typeOf[List[Double]] => OptTypes.ARRAY
      case t if t =:= typeOf[Seq[Double]] => OptTypes.ARRAY
      case _ => optType
    }

    //infer parseOp
    val inferParseOp = {
      if (Option(parseOp).isDefined) {
        parseOp
      } else {
        typeOf[T] match {
          case t if t =:= typeOf[Int] => (s: String) => s.toInt
          case t if t =:= typeOf[Long] => (s: String) => s.toLong
          case t if t =:= typeOf[Double] => (s: String) => s.toDouble
          case _ => (s: String) => s.asInstanceOf[T]
        }
      }
    }

    new RequiredOpt[Any](key, message, if (abrKey == "") None else Some(abrKey), inferParseOp, opt)
  }
}
