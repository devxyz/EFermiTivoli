package it.gov.fermitivoli.model;

/**
 * Created by stefano on 29/06/15.
 */
public class GenericValue<T> {
    private T value;

    public GenericValue() {

    }

    public GenericValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
