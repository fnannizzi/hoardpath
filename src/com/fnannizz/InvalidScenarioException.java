package com.fnannizz;

/**
 * Created by francesca on 11/25/16.
 */
public class InvalidScenarioException extends Exception {
    public InvalidScenarioException(String output) {
        super(output);
    }
}
