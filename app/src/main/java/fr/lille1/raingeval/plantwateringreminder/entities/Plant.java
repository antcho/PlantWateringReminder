package fr.lille1.raingeval.plantwateringreminder.entities;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.lille1.raingeval.plantwateringreminder.activities.MainActivity;


/**
 * Created by anthony on 22/11/16.
 */

@Entity
public class Plant {

    @Id
    private Long id;

    @NotNull
    private String name;
    private int wateringFrequency;
    private int daysSinceLastWatering;
    private long lastWateringDate;
    //private int lastWateringDate;

    public Plant() {

    }




    @Generated(hash = 1939713054)
    public Plant(Long id, @NotNull String name, int wateringFrequency,
            int daysSinceLastWatering, long lastWateringDate) {
        this.id = id;
        this.name = name;
        this.wateringFrequency = wateringFrequency;
        this.daysSinceLastWatering = daysSinceLastWatering;
        this.lastWateringDate = lastWateringDate;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWateringFrequency() {
        return wateringFrequency;
    }

    public void setWateringFrequency(int wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }

    public int getDaysSinceLastWatering() {
        setDaysSinceLastWatering(computeDaysSinceLastWatering());
        return daysSinceLastWatering;
    }

    public void setDaysSinceLastWatering(int daysSinceLastWatering) {
        this.daysSinceLastWatering = daysSinceLastWatering;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public long getLastWateringDate() {
        return this.lastWateringDate;
    }

    public void setLastWateringDate(long lastWateringDate) {
        this.lastWateringDate = lastWateringDate;
    }

    public int computeDaysSinceLastWatering() {
        Date date = new Date();
        //long timeNow = date.getTime();

        long timeDifference = MainActivity.currentTime - lastWateringDate;
        int days = (int) (timeDifference / 86400000);
        return days;
    }



}
