package com.mehrani.Baloot.Exceptions;

public class ProviderNotExistsException extends Exception {
    public ProviderNotExistsException() {
        super("The selected provider does not exists!");
    }
}
