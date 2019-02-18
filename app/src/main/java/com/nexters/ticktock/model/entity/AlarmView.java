package com.nexters.ticktock.model.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class AlarmView {
    @Embedded
    private Alarm alarm;

    @Relation(parentColumn = "id", entityColumn = "alarmId", entity = Step.class)
    private List<Step> steps;
}
