package org.jrosenkranz.opts.options

import org.jrosenkranz.opts.constants.OptTypes
import scala.reflect.runtime.universe._

/**
  * A special for of [[Opt]] where a default value is given
  *
  * @param key the key for this option
  * @param default default value
  * @param message the message for this option
  * @param abrKey the abbreviation key for this option
  * @param parseOp parse operation
  * @param optType the type of this opt to be specified on failure message
  * @tparam T the type of value for this option
  */
@SerialVersionUID(563333959786505484L)
class DefaultOpt[T]
    (
      key: String,
      default: T,
      message: String,
      abrKey: Option[String],
      parseOp: (String) => T,
      optType: OptTypes.Value
    ) extends InputOpt[T](key,message,abrKey) {

  /**
    * this is the default value
    */
  val value: T = default

  override def extractValue(value: String): T = {
    if (default.toString == value) default else parseOp(value)
  }

  override val isRequired: Boolean = false

  override def toString: String = {

    s"--$key$abrFormatted <$typeString> | $defaultMessage" + "\n" +
      s"$messageFormatted" + "\n"
  }

  private val defaultMessage: String = {
    s"default value = ${default match {
      case array: Array[Any] => array.mkString(",")
      case traversable: Traversable[Any] => traversable.mkString(",")
      case _ => default
    }}"
  }

  private val typeString: String = {
    if (optType != OptTypes.UNKNOWN) {
      OptTypes.getName(optType)
    } else {
      default match {
        case array: Array[String] => OptTypes.getName(OptTypes.ARRAY)
        case traversable: Traversable[String] => OptTypes.getName(OptTypes.ARRAY)
        case arrayI: Array[Int] => OptTypes.getName(OptTypes.ARRAY)
        case traversableI: Traversable[Int] => OptTypes.getName(OptTypes.ARRAY)
        case arrayL: Array[Long] => OptTypes.getName(OptTypes.ARRAY)
        case traversableL: Traversable[Long] => OptTypes.getName(OptTypes.ARRAY)
        case arrayD: Array[Double] => OptTypes.getName(OptTypes.ARRAY)
        case traversableD: Traversable[Double] => OptTypes.getName(OptTypes.ARRAY)
        case _ => {
          default.getClass.getSimpleName match {
            case "String" => OptTypes.getName(OptTypes.STRING)
            case "Integer" => OptTypes.getName(OptTypes.INTEGER)
            case "Double" => OptTypes.getName(OptTypes.DOUBLE)
            case "Long" => OptTypes.getName(OptTypes.LONG)
            case _ => default.getClass.getSimpleName
          }
        }
      }
    }
  }
}

object DefaultOpt {
  /**
    * create a [[DefaultOpt]]
    *
    * @param key the [[Opt]] key
    * @param default default value
    * @param message the message associated with this [[Opt]]
    * @param abrKey the abbreviation key
    * @param parseOp parse operation
    * @param optType the type of this opt to be specified on failure message
    * @tparam T the type of value in this [[Opt]]
    * @return a new [[DefaultOpt]]
    */
  def apply[T: TypeTag]
      (
        key: String,
        default: T,
        message: String = "",
        abrKey: String = "",
        parseOp: (String) => T = null,
        optType: OptTypes.Value = OptTypes.UNKNOWN
      ): DefaultOpt[Any] = {

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



    new DefaultOpt[Any](key, default, message, if (abrKey == "") None else Some(abrKey), inferParseOp, optType)
  }
}
