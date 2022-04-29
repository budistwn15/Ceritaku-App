package com.budi.setiawan.storyappbudisetiawan.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.budi.setiawan.storyappbudisetiawan.databinding.FragmentDetailBinding
import com.budi.setiawan.storyappbudisetiawan.view.home.HomeFragment
import com.budi.setiawan.storyappbudisetiawan.view.maps.MapsFragment
import com.bumptech.glide.Glide

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentDetailBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val story = args.story

        binding.apply {
            tvNameStory.text = story.name
            tvDescStory.text = story.description
        }


        Glide.with(requireContext())
            .load(story.imageUrl)
            .into(binding.imgStory)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(
            MapsFragment.KEY_RESULT,
            Bundle().apply { putBoolean(HomeFragment.KEY_FROM_OTHER_SCREEN, true) })
    }

}