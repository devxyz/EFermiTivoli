package it.gov.fermitivoli.dao;

import android.content.Context;

/**
 * Created by stefano on 09/06/15.
 */
public interface FermiAppDBHelperRun {
    public void run(DaoSession session, Context ctx) throws Throwable;
}
