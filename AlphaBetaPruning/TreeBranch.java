package io.hbt.java.AlphaBetaPruning;

import java.util.ArrayList;

/**
 * @author Harri Bell-Thomas <ahb36@cam.ac.uk>
 */
public class TreeBranch extends TreeNode {
    private ArrayList<TreeNode> children;
    private int alpha, beta;

    public TreeBranch(TreeNode ... ns) {
        this.children = new ArrayList<>();
        for(TreeNode n : ns) this.children.add(n);
        this.alpha = Integer.MIN_VALUE;
        this.beta = Integer.MAX_VALUE;
    }

    public ArrayList<TreeNode> getChildren() { return this.children; }
    public void addChild(TreeNode t) { this.children.add(t); }

    public void setAlpha(int v) { this.alpha = v; }
    public void setBeta(int v) { this.beta = v; }
    public void setAlphaBeta(int a, int b) {
        this.setAlpha(a);
        this.setBeta(b);
    }

    public int getAlpha() { return this.alpha; }
    public int getBeta() { return this.beta; }
}
