package com.example.dogtraininglog.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.dogtraininglog.MainActivity;
import com.example.dogtraininglog.database.entities.Dog;
import com.example.dogtraininglog.database.entities.User;
import com.example.dogtraininglog.database.typeConverters.LocalDateTypeConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Here we have defined our database.
@TypeConverters(LocalDateTypeConverter.class)
@Database(entities = {DogLog.class, User.class, Dog.class}, version = 7, exportSchema = false)
public abstract class DogTrainingDatabase extends RoomDatabase {

    /*Our table name contastants*/
    public static final String USER_TABLE = "usertable";
    private static final String DATABASE_NAME = "DogTrainingDatabase";
    public static final String DOG = "Dog";
    public static final String DOG_LOG_TABLE = "dogLogTable";

    /*DAO accessors*/
    public abstract DogDAO dogDAO();
    public abstract DogTrainingLogDAO dogTrainingLogDAO();
    public abstract UserDAO userDAO();

    /*Holds a single database instance*/
    private static volatile DogTrainingDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //Here is our singleton method to get a database instance.
    static DogTrainingDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DogTrainingDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    DogTrainingDatabase.class,
                                    DATABASE_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.i(MainActivity.TAG, "DATABASE CREATED");
            databaseWriteExecutor.execute(() -> {
                UserDAO dao = INSTANCE.userDAO();
                dao.deleteAll();
                User admin = new User("admin1", "admin1");
                admin.setAdmin(true);
                dao.insert(admin);
                User testUser1 = new User("testuser1", "testuser1");
                dao.insert(testUser1);
            });
        }
    };

}
