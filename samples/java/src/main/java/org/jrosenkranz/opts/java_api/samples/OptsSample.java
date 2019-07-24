package org.jrosenkranz.opts.java_api.samples;

import org.jrosenkranz.opts.java_api.Opts;
import org.jrosenkranz.opts.java_api.options.OptBuilder;
import org.jrosenkranz.opts.java_api.set.OptSet;

import java.util.Arrays;
import java.util.List;

public class OptsSample {

    private static OptSet optSet = OptSet.build(
            OptBuilder.defaultOpt("topics", Arrays.asList("sample1","sample2","sample3"))
                    .message("kafka topics comma separated")
                    .parseOp(s -> Arrays.asList(s.split(",")))
                    .abrev("t"),
            OptBuilder.defaultOpt("inputPath","./").message("path to input"),
            OptBuilder.defaultOpt("delimiter",",").message("kafka message delimiter"),
            OptBuilder.flagOpt("help").message("this will display all commands as a list"),
            OptBuilder.commandOpt("update").message("update the system")
                    .addChild(OptBuilder.requiredOpt("host").message("kafka host"))
                    .addChild(OptBuilder.defaultOpt("port",2181).message("kafka port").parseOp(Integer::valueOf))
                    .addChild(OptBuilder.flagOpt("help").message("this will display the update help")),
            OptBuilder.commandOpt("delete").message("delete the system")
                    .addChild(OptBuilder.requiredOpt("name").message("item to delete"))
    );

    public static void main(String... args) {
        Opts options = new Opts(optSet,args);

        if (options.<Boolean>getAs("help")) {
            System.out.println(optSet);
            System.exit(0);
        }

        List<String> topics = options.getAs("topics");
        String inputPath = options.getAs("inputPath");
        String delimiter = options.getAs("delimiter");
        topics.forEach(System.out::println);
        System.out.println(inputPath);
        System.out.println(delimiter);

        if (options.commands.isPresent()) {

            switch (options.commands.get().get(0)) {
                case "update":
                    String host = options.getAs("host");
                    int port = options.getAs("port");
                    boolean help = options.getAs("help");
                    System.out.println(host);
                    System.out.println(port);
                    System.out.println(help);
                    break;

                case "delete":
                    String name = options.getAs("name");
                    System.out.println(name);
                    break;
            }


        }
    }
}
