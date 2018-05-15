package io.hbt.java.AC3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Map;


/**
 * Academic implementation of the AC-3 algorithm.
 * Problem is hard-coded in the main() function.
 */
public class AC3 {

    private HashMap<Integer, Node> nodes = new HashMap<>();
    private HashMap<Integer, Domain> domains = new HashMap<>();
    private HashSet<Constraint> constraints = new HashSet<>();

    /**
     * Create new node objects from int IDs.
     * @param ns Variable number of int IDs.
     */
    public void newNodes(int ... ns) {
        for(int n : ns) this.nodes.put(n, new Node(n));
    }


    /**
     * For a node i, set the values in D_i, its domain.
     * @param n The node's ID.
     * @param vs Variable number of allowed values.
     */
    public void setNodeDomain(int n, Value ... vs) {
        this.domains.put(n, new Domain(vs));
    }


    /**
     * Add a new constraint to the set of variables. Directed arc.
     * @param f 'From' variable.
     * @param t 'To' variable.
     */
    public void addConstraint(int f, int t) {
        if(f != t) {
            Constraint c = new Constraint(f, t);
            this.constraints.add(c);
            if(this.nodes.containsKey(f)) this.nodes.get(f).addConstraint(c);
            if(this.nodes.containsKey(t)) this.nodes.get(t).addConstraint(c);
        }
    }


    /**
     * Undirected constraint initialiser.
     * @param i ID of first node.
     * @param j ID of second node.
     */
    public void addBiConstraint(int i, int j) {
        this.addConstraint(i, j);
        this.addConstraint(j, i);
    }


    /**
     * Function dictating allowed neighbour nodes according to relationship.
     * We're using the no-same-adjacent constraint rule.
     * @param a First value.
     * @param b Second value.
     * @return If they're permitted to be neighbours.
     */
    private boolean compatibleValues(Value a, Value b) {
        return (a != b);
    }


    /**
     * Adjusts 'from' domain to enforce consistency.
     * @param i 'From' node.
     * @param j 'To' node.
     * @return Whether the domain has changed.
     */
    private boolean removeInconsistencies(int i, int j) {
        boolean hasChanged = false;

        Domain Di = this.domains.get(i);
        Domain Dj = this.domains.get(j);

        ArrayList<Value> toRemove = new ArrayList<>();
        for(Value v : Di.getValues()) {
            boolean foundCompatible = false;
            for(Value w : Dj.getValues()) {
                if(this.compatibleValues(v, w)) {
                    foundCompatible = true;
                    break;
                }
            }
            if(!foundCompatible) {
                toRemove.add(v);
                hasChanged = true;
            }
        }
        for(Value v : toRemove) Di.removeValue(v);

        return hasChanged;
    }


    /**
     * Given a well defined graph, run constraint the AC-3 constraint propagation algorithm.
     * Prints out domain state before and after.
     */
    public void runArcConsistencyEnforcing() {
        this.printState();

        PriorityQueue<Constraint> toCheck = new PriorityQueue<>();
        for(Constraint c : this.constraints) toCheck.add(c);

        Constraint current = toCheck.poll();
        while(current != null) {

            if(this.removeInconsistencies(current.getFrom(), current.getTo())) {
                if(this.nodes.containsKey(current.getFrom())) {
                    Node i = this.nodes.get(current.getFrom());
                    for(Constraint c : i.getAncestors()) toCheck.add(c);
                }
            }

            current = toCheck.poll();
        }

        System.out.println("-- After -- ");
        this.printState();
    }


    /**
     * Prints out the current state of all domains to the console.
     */
    public void printState() {
        for(Map.Entry<Integer, Domain> entry : this.domains.entrySet()) {
            HashSet<Value> e = entry.getValue().getValues();
            String d = "";
            for(Value v : e) d += v.name() + ", ";
            if(d.length() > 0) d = d.substring(0, d.length() - 2);
            d = "{" + d + "}";
            System.out.println(entry.getKey() + ": " + d);
        }
    }


    /**
     * CLI entry point.
     * Builds graph and runs the AC-3 algorithm at various points in the assignment execution.
     * Problem as described on the problem sheet.
     * Question 5.1 @ http://www.cl.cam.ac.uk/teaching/1718/ArtInt/ai1-problems-2018.pdf
     * @param args CLI arguments. Not used.
     */
    public static void main(String[] args) {
        AC3 x = new AC3();
        x.newNodes(1, 2, 3, 4, 5, 6, 7, 8);
        x.addBiConstraint(1, 2);
        x.addBiConstraint(1, 3);
        x.addBiConstraint(1, 4);
        x.addBiConstraint(2, 4);
        x.addBiConstraint(2, 6);
        x.addBiConstraint(3, 4);
        x.addBiConstraint(3, 7);
        x.addBiConstraint(4, 5);
        x.addBiConstraint(5, 6);
        x.addBiConstraint(5, 7);
        x.addBiConstraint(6, 7);
        x.addBiConstraint(6, 8);
        x.addBiConstraint(7, 8);

        System.out.println("-------------------------");
        System.out.println("State 0");
        x.setNodeDomain(1, Value.B, Value.R, Value.C);
        x.setNodeDomain(2, Value.B, Value.R, Value.C);
        x.setNodeDomain(3, Value.B, Value.R, Value.C);
        x.setNodeDomain(4, Value.B, Value.R, Value.C);
        x.setNodeDomain(5, Value.B, Value.R, Value.C);
        x.setNodeDomain(6, Value.B, Value.R, Value.C);
        x.setNodeDomain(7, Value.B, Value.R, Value.C);
        x.setNodeDomain(8, Value.B, Value.R, Value.C);
        x.runArcConsistencyEnforcing();
        System.out.println();

        System.out.println("-------------------------");
        System.out.println("State 1");
        x.setNodeDomain(1, Value.R);
        x.setNodeDomain(2, Value.B, Value.R, Value.C);
        x.setNodeDomain(3, Value.B, Value.R, Value.C);
        x.setNodeDomain(4, Value.B, Value.R, Value.C);
        x.setNodeDomain(5, Value.B, Value.R, Value.C);
        x.setNodeDomain(6, Value.B, Value.R, Value.C);
        x.setNodeDomain(7, Value.B, Value.R, Value.C);
        x.setNodeDomain(8, Value.B, Value.R, Value.C);
        x.runArcConsistencyEnforcing();
        System.out.println();

        System.out.println("-------------------------");
        System.out.println("State 2");
        x.setNodeDomain(1, Value.R);
        x.setNodeDomain(2, Value.B, Value.R, Value.C);
        x.setNodeDomain(3, Value.B, Value.R, Value.C);
        x.setNodeDomain(4, Value.C);
        x.setNodeDomain(5, Value.B, Value.R, Value.C);
        x.setNodeDomain(6, Value.B, Value.R, Value.C);
        x.setNodeDomain(7, Value.B, Value.R, Value.C);
        x.setNodeDomain(8, Value.B, Value.R, Value.C);
        x.runArcConsistencyEnforcing();
        System.out.println();

        System.out.println("-------------------------");
        System.out.println("State 3");
        x.setNodeDomain(1, Value.R);
        x.setNodeDomain(2, Value.B, Value.R, Value.C);
        x.setNodeDomain(3, Value.B, Value.R, Value.C);
        x.setNodeDomain(4, Value.C);
        x.setNodeDomain(5, Value.R);
        x.setNodeDomain(6, Value.B, Value.R, Value.C);
        x.setNodeDomain(7, Value.B, Value.R, Value.C);
        x.setNodeDomain(8, Value.B, Value.R, Value.C);
        x.runArcConsistencyEnforcing();
        System.out.println();

        System.out.println("-------------------------");
        System.out.println("State 4");
        x.setNodeDomain(1, Value.R);
        x.setNodeDomain(2, Value.B, Value.R, Value.C);
        x.setNodeDomain(3, Value.B, Value.R, Value.C);
        x.setNodeDomain(4, Value.C);
        x.setNodeDomain(5, Value.R);
        x.setNodeDomain(6, Value.B, Value.R, Value.C);
        x.setNodeDomain(7, Value.B, Value.R, Value.C);
        x.setNodeDomain(8, Value.C);
        x.runArcConsistencyEnforcing();
        System.out.println();

        System.out.println("-------------------------");
        System.out.println("State 5");
        x.setNodeDomain(1, Value.R);
        x.setNodeDomain(2, Value.B, Value.R, Value.C);
        x.setNodeDomain(3, Value.B, Value.R, Value.C);
        x.setNodeDomain(4, Value.C);
        x.setNodeDomain(5, Value.R);
        x.setNodeDomain(6, Value.B);
        x.setNodeDomain(7, Value.B, Value.R, Value.C);
        x.setNodeDomain(8, Value.C);
        x.runArcConsistencyEnforcing();
        System.out.println();
    }

    /*

    Program Output:
    -------------------------
    State 0
    1: {R, C, B}
    2: {R, C, B}
    3: {R, C, B}
    4: {R, C, B}
    5: {R, C, B}
    6: {R, C, B}
    7: {R, C, B}
    8: {R, C, B}
    -- After --
    1: {R, C, B}
    2: {R, C, B}
    3: {R, C, B}
    4: {R, C, B}
    5: {R, C, B}
    6: {R, C, B}
    7: {R, C, B}
    8: {R, C, B}

    -------------------------
    State 1
    1: {R}
    2: {R, C, B}
    3: {R, C, B}
    4: {R, C, B}
    5: {R, C, B}
    6: {R, C, B}
    7: {R, C, B}
    8: {R, C, B}
    -- After --
    1: {R}
    2: {C, B}
    3: {C, B}
    4: {C, B}
    5: {R, C, B}
    6: {R, C, B}
    7: {R, C, B}
    8: {R, C, B}

    -------------------------
    State 2
    1: {R}
    2: {R, C, B}
    3: {R, C, B}
    4: {C}
    5: {R, C, B}
    6: {R, C, B}
    7: {R, C, B}
    8: {R, C, B}
    -- After --
    1: {R}
    2: {B}
    3: {B}
    4: {C}
    5: {R, B}
    6: {R, C}
    7: {R, C}
    8: {R, C, B}

    -------------------------
    State 3
    1: {R}
    2: {R, C, B}
    3: {R, C, B}
    4: {C}
    5: {R}
    6: {R, C, B}
    7: {R, C, B}
    8: {R, C, B}
    -- After --
    1: {}
    2: {}
    3: {}
    4: {}
    5: {}
    6: {}
    7: {}
    8: {}

    -------------------------
    State 4
    1: {R}
    2: {R, C, B}
    3: {R, C, B}
    4: {C}
    5: {R}
    6: {R, C, B}
    7: {R, C, B}
    8: {C}
    -- After --
    1: {}
    2: {}
    3: {}
    4: {}
    5: {}
    6: {}
    7: {}
    8: {}

    -------------------------
    State 5
    1: {R}
    2: {R, C, B}
    3: {R, C, B}
    4: {C}
    5: {R}
    6: {B}
    7: {R, C, B}
    8: {C}
    -- After --
    1: {}
    2: {}
    3: {}
    4: {}
    5: {}
    6: {}
    7: {}
    8: {}


     */
}
