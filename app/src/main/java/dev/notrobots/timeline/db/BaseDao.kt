package dev.notrobots.timeline.db

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.OnConflictStrategy.Companion.REPLACE

interface BaseDao<T> {
    @Insert
    suspend fun insert(item: T): Long

    @Insert
    suspend fun insert(vararg items: T): List<Long>

    @Insert
    suspend fun insert(items: Set<T>): List<Long>

    @Insert
    suspend fun insert(items: List<T>): List<Long>

    @Insert(onConflict = REPLACE)
    suspend fun insertOrReplace(item: T): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertOrReplace(items: List<T>): List<Long>

    @Insert(onConflict = REPLACE)
    suspend fun insertOrReplace(items: Set<T>): List<Long>

    @Insert(onConflict = REPLACE)
    suspend fun insertOrReplace(vararg items: T): List<Long>

    @Insert(onConflict = IGNORE)
    suspend fun insertOrIgnore(item: T): Long

    @Insert(onConflict = IGNORE)
    suspend fun insertOrIgnore(items: List<T>): List<Long>

    @Insert(onConflict = IGNORE)
    suspend fun insertOrIgnore(items: Set<T>): List<Long>

    @Insert(onConflict = IGNORE)
    suspend fun insertOrIgnore(vararg items: T): List<Long>

    @Upsert
    suspend fun insertOrUpdate(item: T): Long

    @Upsert
    suspend fun insertOrUpdate(items: List<T>): List<Long>

    @Upsert
    suspend fun insertOrUpdate(items: Set<T>): List<Long>

    @Upsert
    suspend fun insertOrUpdate(vararg items: T): List<Long>

    @Update
    suspend fun update(item: T): Int

    @Update
    suspend fun update(items: List<T>): Int

    @Update
    suspend fun update(items: Set<T>): Int

    @Update
    suspend fun update(vararg items: T): Int

    @Delete
    suspend fun delete(item: T): Int

    @Delete
    suspend fun delete(items: List<T>): Int

    @Delete
    suspend fun delete(items: Set<T>): Int

    @Delete
    suspend fun delete(vararg items: T): Int
}