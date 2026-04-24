package com.demo.coffeerecorder.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CoffeeRecordEntity.class}, version = 1, exportSchema = false)
public abstract class CoffeeDatabase extends RoomDatabase {
    private static volatile CoffeeDatabase INSTANCE;

    public abstract CoffeeRecordDao coffeeRecordDao();

    public static CoffeeDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CoffeeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CoffeeDatabase.class,
                            "coffee_recorder.db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
