package io.hbt.java.AC3;

import java.util.ArrayList;

public class Node {
    private int ID;
    private ArrayList<Constraint> ancestors = new ArrayList<>();
    private ArrayList<Constraint> children = new ArrayList<>();
    public Node(int id) { this.ID = id; }
    public int getID() { return this.ID; }
    public void addConstraint(Constraint c) {
        if(this.ID == c.getFrom()) this.children.add(c);
        else if(this.ID == c.getTo()) this.ancestors.add(c);
    }
    public ArrayList<Constraint> getAncestors() { return this.ancestors; }
}