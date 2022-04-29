package com.budi.setiawan.storyappbudisetiawan.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.budi.setiawan.storyappbudisetiawan.data.item.StoryItems

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<StoryItems>)

    @Query("SELECT * FROM story_items")
    fun getStories(): PagingSource<Int, StoryItems>

    @Query("DELETE FROM story_items")
    suspend fun deleteAllStories()
}