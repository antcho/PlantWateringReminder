package fr.lille1.raingeval.plantwateringreminder;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import fr.lille1.raingeval.plantwateringreminder.entities.DaoMaster;
import fr.lille1.raingeval.plantwateringreminder.entities.DaoSession;

/**
 * Created by anthony on 23/11/16.
 */

public class App extends Application {


    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-database");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
