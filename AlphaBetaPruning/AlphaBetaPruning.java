package io.hbt.java.AlphaBetaPruning;

import java.util.ArrayList;

/**
 * @author Harri Bell-Thomas <ahb36@cam.ac.uk>
 */
public class AlphaBetaPruning {

    /* Actor annotation */
    public enum Actor { MAX, MIN }

    /**
     * Switches between MAX and MIN.
     * @param a Actor to switch from.
     * @return Other actor.
     */
    private static Actor switchAgent(Actor a) {
        if(a == Actor.MAX) return Actor.MIN;
        else return Actor.MAX;
    }

    /**
     * Converts MAX_INT and MIN_INT to Infinity and -Infinity.
     * @param i Integer to prettify.
     * @return Prettified string.
     */
    private static String prettyIntPrint(int i) {
        if(i == Integer.MAX_VALUE) return "Infinity";
        else if(i == Integer.MIN_VALUE) return "-Infinity";
        else return "" + i;
    }

    /**
     * Recursive alpha-beta pruning algorithm.
     * @param t Tree to prune.
     * @param a Actor starting at the root of the tree.
     * @throws PruningException Errors produced during the alpha-beta execution.
     * @return Returned via the TreeNode reference passed in.
     */
    public static void prune(TreeNode t, Actor a) throws PruningException {
        if(t instanceof TreeLeaf) {
            // Value already set as it's a leaf.
        }
        else if(t instanceof TreeBranch) {

            // Start at the root and start DFS with pruning.
            // Alpha and Beta already set in the constructor of TreeBranch.
            TreeBranch root = (TreeBranch) t;
            ArrayList<TreeNode> children = root.getChildren();
            AlphaBetaPruning.Actor adversary = switchAgent(a);

            int count = 1;
            int numChildren = children.size();

            // Explore each child in turn.
            for(TreeNode n : children) {

                // If we have a branch, recursively prune to generate its value.
                if (n instanceof TreeBranch) {
                    TreeBranch b = (TreeBranch) n;
                    b.setAlphaBeta(root.getAlpha(), root.getBeta());
                    AlphaBetaPruning.prune(b, adversary);
                }

                // Sanity check.
                if (!n.hasValue()) {
                    throw new PruningException("Recursive pruning call failed.");
                }

                // Deal with passing alpha and beta back to the root.
                if (a == Actor.MAX) {
                    root.setAlpha(Math.max(n.getValue(), root.getAlpha()));
                } else {
                    root.setBeta(Math.min(n.getValue(), root.getBeta()));
                }

                // Update the root's value.
                if (!root.hasValue()) root.setValue(n.getValue());
                else if (a == Actor.MAX) root.setValue(Math.max(n.getValue(), root.getValue()));
                else root.setValue(Math.min(n.getValue(), root.getValue()));


                // Do pruning if beta <= alpha and not the last child.
                if(root.getBeta() <= root.getAlpha() && count < numChildren) {
                    System.out.println("---");
                    System.out.println("Pruning node after child #" + count + " with value " + root.getValue());
                    System.out.println("Alpha: " + prettyIntPrint(root.getAlpha()) + ", Beta: " + prettyIntPrint(root.getBeta()));
                    return; // Don't explore remaining children.
                }

                count++;
            }

        }

        else throw new PruningException("Invalid Input. The tree is neither a branch or a node!");
    }

    /**
     * Generates a regular (all nodes obey the branching factor precisely) tree from an int array.
     * Assumes numbers.length = branchFactor^n.
     * @param numbers Integer array input, left to right.
     * @param branchFactor The tree's branching factor.
     * @return The root node of the tree.
     */
    public static TreeNode buildRegularTree(int[] numbers, int branchFactor) throws PruningException {

        // Check number of ints we have is a power of the branch factor.
        double logLengthCheck = Math.log(numbers.length) / Math.log(branchFactor);
        if ((logLengthCheck != Math.floor(logLengthCheck)) || Double.isInfinite(logLengthCheck)) {
            throw new PruningException("Number of ints in array not a power of the branching factor.");
        }

        // No ints, no tree.
        else if(numbers.length == 0) {
            throw new PruningException("Failed to auto build tree from array.");
        }

        // Only one int goes into a TreeLeaf.
        else if(numbers.length == 1) {
            return new TreeLeaf(numbers[0]);
        }

        // General case.
        else {

            // Convert ints to TreeLeafs and put in their subtrees at level n-1.
            ArrayList<TreeNode> items = new ArrayList<>();
            for(int i = 0; i < numbers.length / branchFactor; i++) {
                TreeBranch b = new TreeBranch();
                for (int j = 0; j < branchFactor; j++) {
                    b.addChild(new TreeLeaf(numbers[i*branchFactor + j]));
                }
                items.add(b);
            }

            // Iteratively creates super trees until we get to the root.
            while(items.size() > 1) {
                ArrayList<TreeNode> parents = new ArrayList<>();
                for(int i = 0; i < items.size() / branchFactor; i++) {
                    TreeBranch b = new TreeBranch();
                    for (int j = 0; j < branchFactor; j++) {
                        b.addChild(items.get(i*branchFactor + j));
                    }
                    parents.add(b);
                }
                items = parents;
            }

            // Return the root.
            return items.get(0);
        }
    }


    /**
     * Program entry point.
     * @param args Call arguments.
     */
    public static void main(String[] args) {
        try {

            // Sample tree.
            // Tree graphic at https://hbt.io/ygHX9T+
            // Generated from http://inst.eecs.berkeley.edu/~cs61b/fa14/ta-materials/apps/ab_tree_practice/

            int[] leaves1 = new int[] {1,-15,2,19,18,23,4,3};
            int[] leaves2 = new int[] {2,1,7,8,9,10,-2,5};
            int[] leaves3 = new int[] {-1,-30,4,7,20,-1,-1,-5};
//            int[] leaves = new int[] {-16,17,-12,-19,-9,-5,-11,-6,17,-2,-1,16,1,-10,8,8,5,13,-20,-6,8,1,16,3,-4,-3,9};
            TreeNode t1 = AlphaBetaPruning.buildRegularTree(leaves1, 2);
            TreeNode t2 = AlphaBetaPruning.buildRegularTree(leaves2, 2);
            TreeNode t3 = AlphaBetaPruning.buildRegularTree(leaves3, 2);

            TreeBranch root = new TreeBranch(t1, t2, t3);

            // Prune!
            AlphaBetaPruning.prune(root, Actor.MIN);
            System.out.println("---");
            System.out.println("Successfully pruned.");
            System.out.println("Optimum for First Player = " + root.getValue());
            if(root instanceof TreeBranch) {
                TreeBranch branch = (TreeBranch) root;
                System.out.println("[Alpha: " + prettyIntPrint(branch.getAlpha()) + ", Beta: " + prettyIntPrint(branch.getBeta()) + "]");
            }
            System.out.println("---");
        }
        catch(PruningException pe) {
            System.out.println(pe.getErrorMessage());
        }

        /*
         * Sample Output:
         *
         * ---
         * Pruning node after child #1 with value 8
         * Alpha: 8, Beta: 8
         * ---
         * Pruning node after child #1 with value 8
         * Alpha: 8, Beta: 8
         * ---
         * Successfully pruned.
         * Optimum for First Player = 8
         * [Alpha: 8, Beta: Infinity]
         * ---
         */
    }
}
