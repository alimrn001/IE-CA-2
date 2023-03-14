package com.mehrani.Baloot.Exceptions;

public class CommentNotExistsException extends Exception {
    public CommentNotExistsException() {
        super("The selected comment does not exist!");
    }
}
