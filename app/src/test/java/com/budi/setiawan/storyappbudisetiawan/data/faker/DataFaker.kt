package com.budi.setiawan.storyappbudisetiawan.data.faker

import androidx.paging.PagingData
import com.budi.setiawan.storyappbudisetiawan.data.item.StoryItems
import com.budi.setiawan.storyappbudisetiawan.data.response.StoryItem

object DataFaker {
    fun dataStoryItems(): List<StoryItem> {
        val listItems = mutableListOf<StoryItem>()
        repeat(10) {
            listItems.add(
                StoryItem(
                    photoUrl = "https://www.dicoding-$it.com",
                    createdAt = "createdAt-$it",
                    name = "name-$it",
                    description = "desc-$it",
                    lon = it.toDouble(),
                    id = it.toString(),
                    lat = it.toDouble(),
                )
            )
        }
        return listItems
    }

    fun pagingDataStoryItems(): PagingData<StoryItems> = PagingData.from(getListStoryItems())

    fun getListStoryItems(): List<StoryItems> {
        val story = mutableListOf<StoryItems>()
        dataStoryItems().forEach {
            story.add(
                StoryItems(
                    id = it.id,
                    name = it.name,
                    createdAt = it.createdAt,
                    imageUrl = it.photoUrl,
                    description = it.description,
                    lat = it.lat,
                    lon = it.lon
                )
            )
        }
        return story
    }
}