package it.gov.fermitivoli.util;

import java.util.concurrent.Callable;

/**
 * Versione parametrica di callable
 */
public interface CallableParameter<T, P> extends Callable<T> {
    public void setParameter(T param);

}
