package com.mehrani.Baloot.Exceptions;

public class ItemAlreadyExistsInBuyListException extends Exception {
    public ItemAlreadyExistsInBuyListException() {
        super("The selected item already exists in your buy list!");
    }
}
