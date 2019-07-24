package org.jrosenkranz.opts.java_api.set;

import org.jrosenkranz.opts.java_api.Opts;
import org.jrosenkranz.opts.options.Opt;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.*;

public class OptSet {
    private org.jrosenkranz.opts.set.OptSet optSet;

    @SafeVarargs
    public static OptSet build(Opt<Object>... options) {
        return new OptSet(options);
    }

    @SafeVarargs
    private OptSet(Opt<Object>... options) {
        List<Opt<Object>> list = new ArrayList<>(Arrays.asList(options));
        Seq<Opt<Object>> seq = scala.collection.JavaConverters.collectionAsScalaIterableConverter(list).asScala().toSeq();
        this.optSet = new org.jrosenkranz.opts.set.OptSet(seq);
    }

    public Optional<Opts> parse(String[] args, boolean commandRequired) {
        Option<org.jrosenkranz.opts.Opts> opts = optSet.parse(args, commandRequired);

        if (opts.isDefined()) {
            Map<String, Object> result = new HashMap<>();
            Set<String> keys = JavaConverters.setAsJavaSetConverter(opts.get().keySet()).asJava();
            for (String key : keys) {
                result.put(key,opts.get().get(key).get());
            }

            Optional<List<String>> commands;

            if (opts.get().commands().isDefined()) {
                String[] cmdAsArray = opts.get().commands().get();
                List<String> cmdAsList = Arrays.asList(cmdAsArray);
                commands = Optional.of(cmdAsList);
            } else {
                commands = Optional.empty();
            }

            return Optional.of(new Opts(result,commands));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Opts> parse(String[] args) {
        return parse(args,false);
    }

    @Override
    public String toString() {
        return optSet.toString();
    }
}
