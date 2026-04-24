package com.demo.coffeerecorder.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CoffeeRecordDao {
    @Query("SELECT * FROM coffee_records ORDER BY drankAt DESC")
    LiveData<List<CoffeeRecordEntity>> observeAllRecords();

    @Query("SELECT * FROM coffee_records WHERE id = :recordId LIMIT 1")
    CoffeeRecordEntity getRecordById(long recordId);

    @Insert
    long insert(CoffeeRecordEntity record);

    @Update
    void update(CoffeeRecordEntity record);

    @Delete
    void delete(CoffeeRecordEntity record);

    @Query("SELECT COUNT(*) FROM coffee_records")
    int countRecords();
}
