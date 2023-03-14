package com.mehrani.Baloot.Exceptions;

public class NegativeCreditAddingException extends Exception {
    public NegativeCreditAddingException() {
        super("Cannot add non-positive value to credit!");
    }
}
