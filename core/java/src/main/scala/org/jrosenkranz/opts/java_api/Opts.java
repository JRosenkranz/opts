package org.jrosenkranz.opts.java_api;

import org.jrosenkranz.opts.java_api.set.OptSet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Opts extends HashMap<String,Object> implements Serializable{

    private static final long serialVersionUID = 7396068778925175570L;
    public Optional<List<String>> commands;

    public Opts(Map<String,Object> map,Optional<List<String>> commands) {
        this.putAll(map);
        this.commands = commands;
    }

    public Opts(OptSet optSet,String[] args,boolean commandRequired) {
        Optional<Opts> opts = optSet.parse(args, commandRequired);

        if (!opts.isPresent()) {
            System.out.println(optSet.toString());
            System.exit(0);
        }

        this.putAll(opts.get());
        this.commands = opts.get().commands;
    }

    public Opts(OptSet optSet, String[] args) {
        this(optSet,args,false);
    }

    public <T> T getAs(String key) {
        return (T)this.get(key);
    }
}
