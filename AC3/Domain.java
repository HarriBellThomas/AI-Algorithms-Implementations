package io.hbt.java.AC3;

import java.util.HashSet;

public class Domain {
    HashSet<Value> possibleValues = new HashSet<>();
    public Domain(Value ... nodes) {
        for(Value n : nodes) this.possibleValues.add(n);
    }
    public HashSet<Value> getValues() { return this.possibleValues; }
    public void removeValue(Value v) { this.possibleValues.remove(v); }
}