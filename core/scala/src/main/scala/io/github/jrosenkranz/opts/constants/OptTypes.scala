package io.github.jrosenkranz.opts.constants

/**
  * An enum which denotes the type to be displayed in a fail message
  */
object OptTypes extends Enumeration {
  type OptTypes = Value

  val STRING, DOUBLE,INTEGER,LONG,ARRAY,UNKNOWN = Value

  def CUSTOM(typeStr: String) : OptTypes = {
    CustomType(typeStr)
  }

  def getName(optTypes: OptTypes): String = {
    optTypes match {
      case STRING => "String"
      case DOUBLE => "Double"
      case INTEGER => "Integer"
      case LONG => "Long"
      case ARRAY => "Array"
      case UNKNOWN => "Unknown"
      case _ => optTypes.asInstanceOf[CustomType].typeStr
    }
  }

  case class CustomType(typeStr: String) extends OptTypes {
    override def id: Int = OptTypes.maxId + 1
  }
}
