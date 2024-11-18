package com.example.myalarmapp.data.room

import androidx.room.Database

@Database(entities = [AlarmEntity::class], version = 1)
abstract class AppDatabase {
    abstract fun alarmDao():AlarmDao
}