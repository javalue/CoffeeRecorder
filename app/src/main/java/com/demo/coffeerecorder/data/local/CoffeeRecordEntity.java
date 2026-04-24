package com.demo.coffeerecorder.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "coffee_records")
public class CoffeeRecordEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String beanName = "";
    public String roaster = "";
    public String origin = "";
    public String drinkType = "";
    public String brewMethod = "";
    public int rating = 0;
    public int cupSizeMl = 0;
    public double priceYuan = 0.0;
    public String notes = "";
    public String photoUri = "";
    public long drankAt = 0L;
}
