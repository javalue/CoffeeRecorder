package com.demo.coffeerecorder.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CoffeeRecordEntity.class}, version = 2, exportSchema = false)
public abstract class CoffeeDatabase extends RoomDatabase {
    private static volatile CoffeeDatabase INSTANCE;
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE coffee_records ADD COLUMN photoUri TEXT");
        }
    };

    public abstract CoffeeRecordDao coffeeRecordDao();

    public static CoffeeDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CoffeeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CoffeeDatabase.class,
                            "coffee_recorder.db"
                    ).addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return INSTANCE;
    }
}
