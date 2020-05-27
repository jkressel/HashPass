package eu.japk.hashpass.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PasswordRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PasswordRecordDAO recordDao();
    private static AppDatabase INSTANCE;


    //make sure that the database exists only as a singleton
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "passwords")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
