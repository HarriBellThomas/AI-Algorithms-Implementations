package io.hbt.java.AC3;

public class Constraint implements Comparable {
    private int from, to;
    public Constraint(int f, int t) {
        this.from = f;
        this.to = t;
    }
    public int getFrom() { return this.from; }
    public int getTo() { return this.to; }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}