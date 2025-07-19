package com.example.gym_tracker.core.database

import androidx.room.TypeConverter
import com.example.gym_tracker.core.database.entity.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilli()
    }
    
    @TypeConverter
    fun fromDuration(value: Long?): Duration? {
        return value?.let { Duration.ofMillis(it) }
    }
    
    @TypeConverter
    fun durationToLong(duration: Duration?): Long? {
        return duration?.toMillis()
    }
    
    @TypeConverter
    fun fromMuscleGroupList(value: List<MuscleGroup>?): String? {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toMuscleGroupList(value: String?): List<MuscleGroup>? {
        return value?.let {
            val listType = object : TypeToken<List<MuscleGroup>>() {}.type
            gson.fromJson(it, listType)
        }
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, listType)
        }
    }
    
    @TypeConverter
    fun fromFitnessGoalList(value: List<FitnessGoal>?): String? {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toFitnessGoalList(value: String?): List<FitnessGoal>? {
        return value?.let {
            val listType = object : TypeToken<List<FitnessGoal>>() {}.type
            gson.fromJson(it, listType)
        }
    }
    
    @TypeConverter
    fun fromEquipmentList(value: List<Equipment>?): String? {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toEquipmentList(value: String?): List<Equipment>? {
        return value?.let {
            val listType = object : TypeToken<List<Equipment>>() {}.type
            gson.fromJson(it, listType)
        }
    }
    
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? {
        return value?.toEpochDay()
    }
    
    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }
}