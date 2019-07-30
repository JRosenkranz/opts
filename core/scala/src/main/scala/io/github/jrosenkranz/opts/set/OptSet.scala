package io.github.jrosenkranz.opts.set

import io.github.jrosenkranz.opts.Opts
import io.github.jrosenkranz.opts.options.{CommandOpt, DefaultOpt, FlagOpt, Opt, RequiredOpt}

import scala.collection.GenTraversableOnce

/**
  * This is the basic schema for a set of options
  *
  * @param parameters the options that go in to this schema
  */
//todo commands values are flattened into the map, they should be accessible through the command so no collisions occur
@SerialVersionUID(6404622318820814820L)
case class OptSet(parameters: Opt[Any]*) extends Set[Opt[Any]] with Serializable {
  protected val set = parameters.toSet

  /**
    * special form of contains where given a key, this will check if this schema contains an option with a name that is
    * equal to that key
    * @param elem the key to check
    * @return true if an option with the specified key is in this set, otherwise false
    */
  def contains(elem: String): Boolean = set.contains(RequiredOpt(elem,parseOp = s => s))

  /**
    * given a [[Opt]] key, get the [[Opt]]
    *
    * @param key [[Opt]] key
    * @return an [[Opt]]
    */
  def apply(key: String): Opt[Any] = get(key).get

  /**
    * given an [[Opt]] key, get an [[Opt]] if the key is contained in this OptSet, otherwise return None
    * @param key [[Opt]] key
    * @return if the key is contained in this OptSet, get an [[Opt]], otherwise return None
    */
  def get(key: String): Option[Opt[Any]] = set.find(p => p.key == key)

  override def contains(elem: Opt[Any]): Boolean = set.contains(elem)

  override def +(elem: Opt[Any]): OptSet = OptSet(set.+(elem).toSeq :_*)

  override def -(elem: Opt[Any]):OptSet = OptSet(set.-(elem).toSeq :_*)

  override def ++(optSet: GenTraversableOnce[Opt[Any]]): OptSet = OptSet((set ++ optSet).toSeq :_*)

  override def iterator: Iterator[Opt[Any]] = set.iterator

  override def toString(): String = {

    val required = {
      if (!set.exists(_.isRequired))
        " "
      else
        " " + set.filter(_.isRequired).map(_.key).map(key => s"--$key <${key.toUpperCase}>").mkString(" ").trim + " "
    }

    val usage = {
      if (set.exists(_.isCmd))
        s"Usage: [cmd]$required[options]"
      else
        s"Usage:$required[options]"
    }

    usage + "\n" +
    set.toArray.filter(!_.isCmd).sortBy(!_.isRequired).map(_.toString).mkString("") + "\n" +
    set.toArray.filter(_.isCmd).map(opt => {
      val childRequired = {
        if (!opt.asInstanceOf[CommandOpt].childOpts.exists(_.isRequired))
          " "
        else
          " " + opt.asInstanceOf[CommandOpt].childOpts.filter(_.isRequired).map(_.key)
            .map(key => s"--$key <${key.toUpperCase}>")
            .mkString(" ")
            .trim + " "
      }
      s"Command: ${opt.key}$required$childRequired" + "\n" + opt.toString
    }).mkString("\n\n")
  }

  /**
    * parse the given args, given this options schema and return an [[Opts]]
    *
    * @param args command line arguments
    * @param cmdRequired parameter to denote if a command is required, if true command is required, otherwise a command
    *                    is not required
    * @return an [[Opts]] if no parsing failure occurred, otherwise return None
    */
  def parse(args: Array[String],cmdRequired: Boolean = false): Option[Opts] = {
    val res = parseParams(this,args)

    (cmdRequired,res._2.isDefined,res._3.nonEmpty) match {
      case (true,true,true) | (false,true,_) => Some(new Opts(res._2.get,res._3))
      case (true,true,false) | (_,false,_) => None
    }
  }

  def parseSkipCommand(args:Array[String]): Option[Opts] = {
    val res = parseParams(this,args.slice(1,args.length),true)

    (false,res._2.isDefined,res._3.nonEmpty) match {
      case (false,true,_) => Some(new Opts(res._2.get,res._3))
      case (_,false,_) => None
    }
  }

  /**
    * parse the given args, given this options schema and return an [[Opts]] with a response
    *
    * @param args command line arguments
    * @param cmdRequired parameter to denote if a command is required, if true command is required, otherwise a command
    *                    is not required
    * @return an [[Opts]] and response if no parsing failure occurred, otherwise return None and response
    */
  def parseWithResponse(args: Array[String],cmdRequired: Boolean = false): (Option[Opts],String) = {
    val res = parseParams(this,args)

    (cmdRequired,res._2.isDefined,res._3.nonEmpty) match {
      case (true,true,true) | (false,true,_) => (Some(new Opts(res._2.get,res._3)),"OK")
      case (true,true,false) | (_,false,_) => (None,res._1)
    }
  }

  private def extractCommand(parameterSet: OptSet, args: Array[String]): (OptSet,Boolean,Array[String]) = {
    //check if we have arguments, it's not possible to have a command if arguments is empty
    if (args.nonEmpty) {

      val (hasMore,passed,newOptSet,arrayCmds) = args.indices.foldLeft((true, true, parameterSet, Array[String]()))((agg, cur) => {
        if (agg._1 && agg._2) {
          agg._3.filter(_.isCmd).find(s => s.key == args(cur)) match {
            //if we found a match, add the command and its child opts to the parameter set
            case Some(opt) => {
              val newCommands = agg._4 ++ Array(opt.key)
              (
                opt.asInstanceOf[CommandOpt].childOpts.exists(_.isCmd), //if we have a command in our child commands, we have to keep going
                true, //no failure has occurred
                OptSet((opt.asInstanceOf[CommandOpt].childOpts ++ agg._3.+(opt)).toSeq: _*), //add our child opts to our current optset
                newCommands
              )
            }
            //if we didn't find a command, make sure we at least found a opt key as the first value
            case None => {
              //check for an opt key, if none is found, fail the parse
              if (args(0).startsWith("-")) //succeed the initial parse of command
                (
                  false,
                  true,
                  parameterSet,
                  agg._4
                )
              else //fail the parse
                (false, false, parameterSet, agg._4)
            }
          }


        } else {
          agg
        }

      })
      (newOptSet,!passed,arrayCmds)
    } else {
      (parameterSet,false,Array[String]())
    }
//      //find all commands in parameter set and check if the first argument matches any of them
//      parameterSet.filter(_.isCmd).find(s => s.key == args(0)) match {
//
//        //if we found a match, add the command and its child opts to the parameter set
//        case Some(opt) => {
//          (OptSet((opt.asInstanceOf[CommandOpt].childOpts ++ parameterSet.+(opt)).toSeq: _*),false,Some(opt.key))
//        }
//        //if we didn't find a command, make sure we at least found a opt key as the first value
//        case None => {
//          //check for an opt key, if none is found, fail the parse
//          if (args(0).startsWith("-")) //succeed the initial parse of command
//            (parameterSet,false,None)
//          else //fail the parse
//            (parameterSet,true,None)
//        }
//      }
//    } else {
//      (parameterSet,false,None)
//    }
  }

  private def extractValueFromParameter(opt: Opt[Any],arg: String)(map: Map[String,Any])(isLast: Boolean): (String,Option[Map[String,Any]]) = {
    //do some initial checks to make sure this is a proper parameter
    if (map.contains(opt.key)) {
      return (s"${opt.key} was specified more than once",None)
    }

    if (isLast && !opt.isInstanceOf[FlagOpt]) {
      return ("last parameter was not specified with a value",None)
    }

    //try to extract a value
    try {
      val extractedValue = opt.extractValue(arg)
      ("", Some(map + (opt.key -> extractedValue)))
    } catch {
      //if an exception is thrown, we could not extract a value from the parameter and we fail parsing
      case _: Exception => {
        (
          s"parameter ${opt.key} cannot be parsed with the given user defined parser",
          None
        )
      }
    }
  }

  private def extractOptsFromArgs(optSet: OptSet, args: Array[String]): (String,Option[Map[String,Any]]) = {
    //create a map from each of our abbreviations found to their corresponding opt key
    val abrevMap = optSet.filter(_.abrKey.isDefined).map(opt => (opt.abrKey.get,opt.key)).toMap

    (args ++ Array("ENDFLAG"))
      .sliding(2,1)
      .zipWithIndex
      .foldLeft(("",Option(Map[String,Any]())))((agg,cur) => {
        val isLast = cur._2 == args.length - 1
        if (agg._2.isDefined) {
          if (optSet.map("--" + _.key).contains(cur._1.head)) {
            extractValueFromParameter(optSet(cur._1.head.substring(2, cur._1.head.length)), cur._1.last)(agg._2.get)(isLast)
          } else {
            val abrevOpt = abrevMap.get(cur._1.head.substring(1,cur._1.head.length))
            //check if we have an abbreviation
            if (abrevOpt.isDefined) {
              val key = abrevOpt.get

              if (optSet.map(_.key).contains(key)) {
                extractValueFromParameter(optSet(key), cur._1.last)(agg._2.get)(isLast)
              } else {
                agg
              }

            } else {
              agg
            }
          }
        } else {
          agg
        }
      })
  }

  def allRequiredOptsProvided(optSet: OptSet,map:Map[String,Any]): Boolean = {
    optSet.filter(_.isRequired).map(_.key).forall(map.contains)
  }

  //todo this could be used for case class conversion
//  def processHasCommand(optSet: OptSet,map: mutable.Map[String,Any]): mutable.Map[String,Any] = {
//    if (!optSet.exists(_.isCmd)) {
//      map ++= optSet.map(x => (x.key,None)).toMap
//    } else {
//      map ++= optSet.filter(!_.isCmd).map(x => (x.key,None))
//      processHasCommand(ChildOptSet(optSet.filter(_.isCmd).flatMap(_.asInstanceOf[CommandOpt].childOpts).toSeq :_*),map)
//    }
//  }

  private def parseParams(parameterSet: OptSet, args: Array[String], skipCommand: Boolean = false): (String,Option[Map[String,Any]],Array[String]) = {
    //first check if we are using a command and return an optset that contains the child opts, whether we failed a parse
    //and the optional command
    val (allOpts,failedCmd,cmd) = if (skipCommand)
      (parameterSet,false,Array[String]())
    else
      extractCommand(parameterSet,args)

    //todo this could be used for case class conversion
//    val fillNulls = parameterSet.filter(opt => opt.isCmd && opt.key != cmd(0)).foldLeft(mutable.Map[String,Any]())((agg,cur) => {
//      val commandOpt = cur.asInstanceOf[CommandOpt]
//      agg ++ processHasCommand(commandOpt.childOpts,agg)
//    })

    //if our command failed we can just return at this point before any other parsing is done
    if (failedCmd) {
      return (parameterSet.toString + "\n" + "the first parameter is not a command or parameter",None,cmd)
    }

    //extract all opts from arguments
    val result = extractOptsFromArgs(allOpts,args)

    //if our map isn't defined, we have failed parsing
    if (result._2.isDefined) {

      //check if all extracted opts that are required exist, if so, pass our parsing
      if (allRequiredOptsProvided(allOpts,result._2.get)) {
        (
          "",
          Some(
            result._2.get ++ allOpts
              .filter(parm => !result._2.get.contains(parm.key) && parm.hasInput && !parm.isRequired)
              .map(x => (x.key,x.asInstanceOf[DefaultOpt[Any]].value))
              .toMap //todo this could be used for case class conversion ++ fillNulls
          ),
          cmd
        )

        //all required arguments did not exist so fail parsing
      } else {
        (parameterSet.toString() + "\n" + "some required parameters were not filled in",None,cmd)
      }

      //we have failed parsing
    } else {
      (parameterSet.toString() + "\n" + result._1,None,cmd)
    }

  }
}
