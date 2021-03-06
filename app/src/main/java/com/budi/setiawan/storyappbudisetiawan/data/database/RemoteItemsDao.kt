package com.budi.setiawan.storyappbudisetiawan.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.budi.setiawan.storyappbudisetiawan.data.item.RemoteItems

@Dao
interface RemoteItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteItems>)

    @Query("SELECT * FROM remote_items WHERE id = :id")
    suspend fun getRemoteKeysById(id: String): RemoteItems?

    @Query("DELETE FROM remote_items")
    suspend fun deleteRemoteKeys()
}