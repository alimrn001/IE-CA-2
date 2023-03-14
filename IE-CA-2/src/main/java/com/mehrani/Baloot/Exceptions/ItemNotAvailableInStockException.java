package com.mehrani.Baloot.Exceptions;

public class ItemNotAvailableInStockException extends Exception {
    public ItemNotAvailableInStockException() {
        super("The selected item is not available in stock!");
    }
}
