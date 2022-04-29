package com.budi.setiawan.storyappbudisetiawan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.budi.setiawan.storyappbudisetiawan.data.item.StoryItems
import com.budi.setiawan.storyappbudisetiawan.databinding.ItemStoryBinding
import com.budi.setiawan.storyappbudisetiawan.view.home.HomeFragmentDirections
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey

class StoryAdapter : PagingDataAdapter<StoryItems, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryItems) {
            Glide.with(binding.root)
                .load(story.imageUrl)
                .signature(ObjectKey(story.imageUrl ?: story.id))
                .into(binding.imgStory)

            binding.apply {
                tvNameStory.text = story.name
                tvDescStory.text = story.description
                root.setOnClickListener {
                    Navigation.findNavController(root).navigate(
                        HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                            story
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<StoryItems>() {
                override fun areItemsTheSame(oldItem: StoryItems, newItem: StoryItems): Boolean =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: StoryItems, newItem: StoryItems): Boolean =
                    oldItem.id == newItem.id
            }
    }
}