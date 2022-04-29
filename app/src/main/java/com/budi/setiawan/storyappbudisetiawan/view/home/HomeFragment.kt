package com.budi.setiawan.storyappbudisetiawan.view.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.budi.setiawan.storyappbudisetiawan.R
import com.budi.setiawan.storyappbudisetiawan.adapter.LoadingStateAdapter
import com.budi.setiawan.storyappbudisetiawan.adapter.StoryAdapter
import com.budi.setiawan.storyappbudisetiawan.data.item.UserItems
import com.budi.setiawan.storyappbudisetiawan.databinding.FragmentHomeBinding
import com.budi.setiawan.storyappbudisetiawan.error.ToastError
import com.budi.setiawan.storyappbudisetiawan.factory.ViewModelFactory
import com.budi.setiawan.storyappbudisetiawan.resource.wrapEspressoIdlingResource
import com.budi.setiawan.storyappbudisetiawan.view.maps.MapsFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var user: UserItems

    private lateinit var adapter: StoryAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var isFromOtherScreen = false

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction()
        setupViewModel()
        playAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu1 -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCreateFragment())
            }
            R.id.menu2 -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menu3 -> {
                viewModel.logout()
            }
            R.id.menu4 -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToMapsFragment(
                        MapsFragment.ACTION_STORIES
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAction(){
        adapter = StoryAdapter().apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0 && isFromOtherScreen.not()) {
                        binding.rvStory.smoothScrollToPosition(0)
                    }
                }
            })
        }

        val adapterWithLoading =
            adapter.withLoadStateFooter(footer = LoadingStateAdapter { adapter.retry() })
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.rvStory.layoutManager = layoutManager
        binding.rvStory.adapter = adapterWithLoading
        adapter.refresh()

        binding.swipeLayout.setOnRefreshListener {
            adapter.refresh()
            binding.swipeLayout.isRefreshing = false
        }

        wrapEspressoIdlingResource {
            lifecycleScope.launch {
                adapter.loadStateFlow.collect {
                    binding.progressBar.isVisible = (it.refresh is LoadState.Loading)
                    binding.tvNoData.isVisible = it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && adapter.itemCount < 1
                    if (it.refresh is LoadState.Error) {
                        ToastError.showToast(
                            requireContext(),
                            (it.refresh as LoadState.Error).error.localizedMessage?.toString()
                                ?: getString(R.string.error_load)
                        )
                    }
                }
            }
        }

        setFragmentResultListener(MapsFragment.KEY_RESULT) { _, bundle ->
            isFromOtherScreen = bundle.getBoolean(KEY_FROM_OTHER_SCREEN, false)
        }
    }

    private fun setupViewModel(){
        viewModel.userItems.observe(viewLifecycleOwner) { userItems ->
            if (userItems?.isLoggedIn == false) {
                findNavController().navigateUp()
            }
            this.user = userItems
        }

        viewModel.stories.observe(viewLifecycleOwner) { stories ->
            adapter.submitData(lifecycle, stories)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            ToastError.showToast(requireContext(), message)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { state ->
            showLoading(state)
        }
    }

    private fun playAnimation() {
        val story = ObjectAnimator.ofFloat(binding.rvStory, View.ALPHA, 1f).setDuration(500)
        AnimatorSet().apply {
            playSequentially(story)
            startDelay = 500
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_FROM_OTHER_SCREEN = "other_screen"
    }
}