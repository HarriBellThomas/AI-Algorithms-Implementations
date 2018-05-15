package io.hbt.java.AlphaBetaPruning;

/**
 * @author Harri Bell-Thomas <ahb36@cam.ac.uk>
 */
public class TreeNode {
    protected int value;
    protected boolean valueAssigned = false;
    public void setValue(int v) { this.value = v; this.valueAssigned = true; }
    public int getValue() { return this.value; }
    public boolean hasValue() { return this.valueAssigned; }
}
