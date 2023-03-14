package com.mehrani.Baloot.Exceptions;

public class WrongVoteValueException extends Exception {
    public WrongVoteValueException() {
        super("Value of vote for a comment must be -1 or 0 or 1 !");
    }
}
