package com.budi.setiawan.storyappbudisetiawan.view.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.budi.setiawan.storyappbudisetiawan.R
import com.budi.setiawan.storyappbudisetiawan.databinding.FragmentMapsBinding
import com.budi.setiawan.storyappbudisetiawan.factory.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val args: MapsFragmentArgs by navArgs()

    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mMaps: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        val action = args.action
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (action == ACTION_PICK_LOCATION) {
            binding.ivMapMarker.isVisible = true
            binding.btnSave.isGone = false
            mapFragment?.getMapAsync(callbackPickLocation)
            return
        }

        mapFragment?.getMapAsync(callbackStories)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.userItems.observe(viewLifecycleOwner) {
            if (it.token != null) {
                viewModel.getStoriesWithLocation(it.token)
            }
        }
    }

    private val callbackStories = OnMapReadyCallback { mMaps ->
        this.mMaps = mMaps
        setMapStyle(mMaps)

        mMaps.uiSettings.isZoomControlsEnabled = true
        mMaps.uiSettings.isIndoorLevelPickerEnabled = true
        mMaps.uiSettings.isCompassEnabled = true
        mMaps.uiSettings.isMapToolbarEnabled = true

        viewModel.stories.observe(viewLifecycleOwner) { stories ->
            if (stories.isNotEmpty()) {
                stories.forEach {
                    if (it.lat != null && it.lon != null) {
                        val coordinate = LatLng(it.lat, it.lon)
                        mMaps.addMarker(MarkerOptions().position(coordinate).title(it.name).snippet(it.description))
                    }
                }
                val latestStory = stories[0]
                val latLng = LatLng(latestStory.lat ?: 0.0, latestStory.lon ?: 0.0)
                mMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5F))
            } else {
                getMyLastLocation(mMaps)
            }
        }
    }

    private val callbackPickLocation = OnMapReadyCallback { mMaps ->
        this.mMaps = mMaps
        var currentPosition = mMaps.cameraPosition.target
        setMapStyle(mMaps)
        getMyLastLocation(mMaps)

        mMaps.setOnCameraMoveStartedListener {
            binding.ivMapMarker.animate().translationY(-40F).start()
        }

        mMaps.setOnCameraIdleListener {
            binding.ivMapMarker.animate().translationY(0F).start()
            currentPosition = mMaps.cameraPosition.target
        }

        binding.btnSave.setOnClickListener {
            setFragmentResult(
                KEY_RESULT,
                Bundle().apply { putParcelable(KEY_LAT_LONG, currentPosition) })
            findNavController().navigateUp()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation(googleMap: GoogleMap) {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            googleMap.isMyLocationEnabled = true
            fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                if (location != null) googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: false -> mMaps?.let { getMyLastLocation(it) }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                    ?: false -> mMaps?.let { getMyLastLocation(it) }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setMapStyle(mMaps: GoogleMap) {
        try {
            val success =
                mMaps.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style
                    )
                )
            if (!success) {
                Log.e(TAG, "Failed to parse map style.")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var TAG: String = MapsFragment::class.java.simpleName
        const val ACTION_STORIES = 0
        const val ACTION_PICK_LOCATION = 1
        const val KEY_RESULT = "key_result"
        const val KEY_LAT_LONG = "key_lat_long"
    }
}