package io.hbt.java.AlphaBetaPruning;

/**
 * @author Harri Bell-Thomas <ahb36@cam.ac.uk>
 */
public class PruningException extends Exception {
    private String message;
    public PruningException(String msg) { this.message = msg; }
    public String getErrorMessage() { return this.message; }
}
