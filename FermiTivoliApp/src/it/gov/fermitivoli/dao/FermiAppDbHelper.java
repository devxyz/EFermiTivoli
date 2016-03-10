package it.gov.fermitivoli.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import it.gov.fermitivoli.api.AbstractActivity;
import it.gov.fermitivoli.async.WaitingAsyncTaskWaitingDialog;

/**
 * Created by stefano on 27/04/15.
 */
public class FermiAppDbHelper {
    final DaoSession session;
    private final DaoMaster.DevOpenHelper helper;
    private final DaoMaster daoMaster;
    private Context ctx;

    public FermiAppDbHelper(Context ctx) {
        this.ctx = ctx;
        helper = new DaoMaster.DevOpenHelper(ctx, "fermi_tivoli_db", null);


        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        session = daoMaster.newSession();
    }

    public static void runOneTransactionSync(AbstractActivity ctx, final FermiAppDBHelperRun run) throws Throwable {
        final FermiAppDbHelper db = new FermiAppDbHelper(ctx.getActivity());
        try {
            db.runInTransaction(run);
        } finally {
            db.close();
        }
    }

    public static <T> T runOneTransactionSync(AbstractActivity ctx, final FermiAppDbHelperCallable<T> run) throws Throwable {
        final FermiAppDbHelper db = new FermiAppDbHelper(ctx.getActivity());
        try {
            return db.runInTransaction(run);
        } finally {
            db.close();
        }
    }


    public static void runOneTransactionAsync(
            final AbstractActivity ctx,
            final FermiAppDBHelperRunAsync run,
            final String title, final String message) {
        final WaitingAsyncTaskWaitingDialog<Void, Void, Void> a = new WaitingAsyncTaskWaitingDialog<Void, Void, Void>(ctx) {
            @Override
            protected String getDialogTitle() {
                return title;
            }

            @Override
            protected String getDialogMessage() {
                return message;
            }

            @Override
            protected void onPostExecuteRunUI_OnSuccess(Void aVoid) {
                run.onPostExecuteRunUI_OnSuccess();
            }

            @Override
            protected void onPostExecuteRunUI_OnError(Throwable error) {
                run.onPostExecuteRunUI_OnError(error);
            }

            @Override
            protected void onCancelledRunUI(Void aVoid) {
                run.onCancelledRunUI();
            }

            @Override
            protected void onCancelledRunUI() {
                run.onCancelledRunUI();
            }

            @Override
            protected Void doInBackgroundImpl(Void... params) throws Throwable {

                runOneTransactionSync(ctx, new FermiAppDBHelperRun() {
                    @Override
                    public void run(DaoSession session, Context ctx) throws Throwable {
                        run.run(session, ctx);
                    }
                });
                return null;
            }


        };
        a.execute();
    }

    public static void runOneTransactionAsync(
            final AbstractActivity ctx,
            final FermiAppDBHelperRun run,
            final String title, final String message) {
        final WaitingAsyncTaskWaitingDialog<Void, Void, Void> a = new WaitingAsyncTaskWaitingDialog<Void, Void, Void>(ctx) {
            @Override
            protected String getDialogTitle() {
                return title;
            }

            @Override
            protected String getDialogMessage() {
                return message;
            }

            @Override
            protected void onPostExecuteRunUI_OnSuccess(Void aVoid) {

            }

            @Override
            protected void onPostExecuteRunUI_OnError(Throwable error) {

            }

            @Override
            protected void onCancelledRunUI(Void aVoid) {

            }

            @Override
            protected void onCancelledRunUI() {

            }

            @Override
            protected Void doInBackgroundImpl(Void... params) throws Throwable {

                runOneTransactionSync(ctx, new FermiAppDBHelperRun() {
                    @Override
                    public void run(DaoSession session, Context ctx) throws Throwable {
                        run.run(session, ctx);
                    }
                });
                return null;
            }


        };
        a.execute();
    }

    public synchronized void close() {
        session.clear();
        daoMaster.getDatabase().close();
        helper.close();
    }

    public synchronized void runInTransaction(final FermiAppDBHelperRun run) throws Throwable {
        MyRunnable r = new MyRunnable(run, session);
        session.runInTx(r);
        if (r.error != null)
            throw r.error;
    }

    public synchronized <T> T runInTransaction(final FermiAppDbHelperCallable<T> run) throws Throwable {
        final DaoSession session = daoMaster.newSession();
        try {
            MyCallable<T> r = new MyCallable<T>(run, session);
            session.runInTx(r);
            if (r.error != null)
                throw r.error;
            else
                return r.result;
        } finally {
            session.clear();
        }

    }

    private class MyRunnable implements Runnable {
        private final FermiAppDBHelperRun run;
        private final DaoSession session;
        private Throwable error;

        public MyRunnable(FermiAppDBHelperRun run, DaoSession session) {
            this.run = run;
            this.session = session;
        }

        @Override
        public void run() {
            try {
                run.run(session, ctx);
            } catch (Throwable throwable) {
                error = throwable;
                throw new IllegalArgumentException(throwable);
            }
        }
    }

    private class MyCallable<T> implements Runnable {
        private final FermiAppDbHelperCallable<T> run;
        private final DaoSession session;
        private Throwable error;
        private T result;

        public MyCallable(FermiAppDbHelperCallable<T> run, DaoSession session) {
            this.run = run;
            this.session = session;
        }

        @Override
        public void run() {
            try {
                result = run.call(session, ctx);
            } catch (Throwable throwable) {
                error = throwable;
                throw new IllegalArgumentException(throwable);
            }
        }
    }
}
