# Parameter Parsing

This is a command line parser to be used in Scala and Java applications. Some features that this parser supports are:

1. Nested Commands
2. Usage generation (including types specified in usage)
3. Options - Default, Required, Flags
4. Any type arguments (arguments can be a type as simple as an integer, but also as complex as your user-defined type)
5. parsing operations for arguments
6. Automatic failure on 
    * value extraction failure (an option was supposed to be an int, but was given with an alpha character) 
    * parse failure 
    * missing options
    * etc.

## including in a project

For Scala

```xml
<dependency>
    <groupId>org.jrosenkranz.opts</groupId>
    <artifactId>opts_2.11</artifactId>
    <version>0.0.1</version>
</dependency>
```

For Java

```xml
<dependency>
    <groupId>org.jrosenkranz.opts</groupId>
    <artifactId>opts</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Immutable Case Class Declaration

_OptSet_ - A Set of Options to use for parsing arguments

_RequiredOpt_ - Options that are required

_DefaultOpt_ - Options that are not required (have a default value)

_FlagOpt_ - Special form of DefaultOpt that specifies true or false (true if provided, otherwise false)

_CommandOpt_ - Option that specifies a command (commands have sub-options called _ChildOptSets_) 


#### Creating a Schema

Scala

```scala
OptSet(
  RequiredOpt("host","kafka host","h"),
  DefaultOpt("port",2181,"kafka port","p"),
  CommandOpt("add","add a topic",ChildOptSet(
    RequiredOpt("topicName","name of topic to add"),
    FlagOpt("help","flag which provides a help message for the add command")
  )),
  CommandOpt("delete","delete a topic",ChildOptSet(
    RequiredOpt("topicName","name of topic to delete"),
    FlagOpt("help","flag which provides a help message for the delete command")
  )),
  CommandOpt("send","send values on a given topic",ChildOptSet(
    RequiredOpt("topicName","name of topic to send values on"),
    RequiredOpt("values","values to send (comma separated)","v",_.split(",").map(_.toDouble))
  ))
)
```

Java

```java
OptSet.build(
        OptBuilder.requiredOpt("host").message("kafka host").abrev("h"),
        OptBuilder.defaultOpt("port",2181).message("kafka port").abrev("p").parseOp(Integer::valueOf),
        OptBuilder.commandOpt("add").message("name of topic to add")
                .addChild(OptBuilder.requiredOpt("topicName").message("name of topic to add"))
                .addChild(OptBuilder.flagOpt("help").message("flag which provides a help message for the add command")),
        OptBuilder.commandOpt("delete").message("delete a topic")
                .addChild(OptBuilder.requiredOpt("topicName").message("name of topic to delete"))
                .addChild(OptBuilder.flagOpt("help").message("flag which provides a help message for the delete command")),
        OptBuilder.commandOpt("send").message("send values on a given topic")
                .addChild(OptBuilder.requiredOpt("topicName").message("name of topic to send values on"))
                .addChild(
                        OptBuilder.requiredOpt("values").message("values to send (comma separated)")
                                .abrev("v")
                                .parseOp(s -> Arrays.stream(s.split(",")).map(Double::valueOf).collect(Collectors.toList()))
                )
);
```

The above schema maps to this if arguments are not provided properly

```text
Usage: [cmd] --host <HOST> [options]
--host, -h <String> | required
    kafka host
--port, -p <Integer> | default value = 2181
    kafka port
		
Command: delete [options]
delete a topic
    --topicName <String> | required
	    name of topic to delete
    --help
	    flag which provides a help message for the delete command
	    
Command: add [options]
add a topic
    --topicName <String> | required
	    name of topic to add
    --help
	    flag which provides a help message for the add command
	    
Command: send [options]
send values on a given topic
    --topicName <String> | required
	    name of topic to send values on
    --values <Array> | required
	    values to send (comma separated)
```

#### Getting options

*fail fast option* (This code will exit and print a message without explicitly asking)

Scala

```scala
val options = Opts(os,args)
```

Java

```java
Opts options = new Opts(os,args)
```

*fail match with options*

Scala

```scala
os.parse(args) match {
  case Some(options) => {
    //passed parse
  }
  case None => {
    //failed parse
  }
}
```

Java

```java
Optional<Opts> optOptions = os.parse(args);

if (optOptions.isPresent()) {
    //passed parse
    Opts options = optOptions.get();
} else {
    //failed parsed
}
```

*fail match with options and failure response* (scala only)

```scala
os.parseWithResponse(args) match {
  case (Some(options),_) => {
    //passed parse
  }
  case (None,res) => {
    //failed parse
  }
}
```

#### Getting Values from options

Scala

```scala
//string can directly get
val host = options("host")
 
//other types require using the as method to properly get as type
val port = options.as[Int]("port")
val values = options.as[Array[Double]]("values")
```

Java

```java
String host = options.getAs("host");
int port = options.getAs("port");
List<Double> values = options.getAs("values");
```

#### Implicit parse operation for DefaultOpt and RequiredOpt (scala only)

For DefaultOpt and RequiredOpt, one need not create a parsing operation when using an argument of type
Int, Long, Double, and String if the argument doesn't require any additional parsing other than a conversion
from string. For DefaultOpt this comes for free since the type is inferred from the defaultValue. For RequiredOpt,
if a type is specified, the parseOp will be generated automatically.

The following is an example

```scala
//no parseOp is required and this option will require an integer
val opt = DefaultOpt("port",2181)

//no parseOp is required and this option will require a Double
val opt = RequiredOpt[Double]("average")
```

#### Implicit type inference for RequiredOpt (scala only)

For RequiredOpt, since one does not explicitly specify a defaultValue as in the case of DefaultOpt,
the type is not known of RequiredOpt and therefore would normally require the setting of the optType parameter
in order to see the type needed for the opt in the error message. With implicit type inference, RequiredOpt
now can infer the type if a parseOp is given

```scala
//the type of this option is inferred from the parseOp
val opt = RequiredOpt("port",parseOp = _.toInt)
```

One may also specify the type bound to have the option find the type

```scala
//the type of this option is taken from the type bound
val opt = RequiredOpt[Int]("port")
```

If an error message is to occur, the following will be printed:

```text
--port <Integer> | required
```

### Arg Parser Class Generation (scala only)

The same schema as above can be built in the form of a class. The main benefit of this is that
is it is by default strongly typed and values can be retrieved without specifying the name

```scala
val options = new ArgParser {
  val host = requiredOpt[String]("host","kafka host","h")
  val port = defaultOpt("port",2181,"kafka port","p")
  
  val add = new commandOpt("add","add a topic") {
    val topicName = requiredOpt[String]("topicName","name of topic to add")
    val help = flagOpt("help","flag which provides a help message for the add command")
  }
  
  val delete = new commandOpt("delete","delete a topic") {
    val topicName = requiredOpt[String]("topicName","name of topic to delete")
    val help = flagOpt("help","flag which provides a help message for the delete command")
  }
  
  val send = new commandOpt("send","send values on a given topic") {
    val topicName = requiredOpt("topicName","name of topic to send values on")
    val values = requiredOpt("values","values to send (comma separated)","v",OptTypes.ARRAY,_.split(",").map(_.toDouble))
  }
}
```

#### Getting options

```scala
//initialize ArgParser and fail fast
options.init(args)

//initialize ArgParser and return true if succeeded
if (options.parse(args)) {
  //use options here since it succeeded and is initialized
} else {
  println(options)
  System.exit(0)
}
```

#### Getting Values from options

```scala
val host = options.host()
val port = options.port()
val values = options.send.values()
```
