package com.budi.setiawan.storyappbudisetiawan.data.item

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "story_items")
data class StoryItems(

    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("imageUrl")
    val imageUrl: String? = null,

    @field:SerializedName("caption")
    val description: String? = null,

    @field:SerializedName("lat")
    val lat: Double? = null,

    @field:SerializedName("lon")
    val lon: Double? = null
) : Parcelable