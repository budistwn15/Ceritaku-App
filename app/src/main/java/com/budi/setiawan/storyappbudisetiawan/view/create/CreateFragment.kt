package com.budi.setiawan.storyappbudisetiawan.view.create

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.budi.setiawan.storyappbudisetiawan.R
import com.budi.setiawan.storyappbudisetiawan.data.item.UserItems
import com.budi.setiawan.storyappbudisetiawan.databinding.FragmentCreateBinding
import com.budi.setiawan.storyappbudisetiawan.error.ToastError
import com.budi.setiawan.storyappbudisetiawan.factory.ViewModelFactory
import com.budi.setiawan.storyappbudisetiawan.view.camera.CameraActivity
import com.budi.setiawan.storyappbudisetiawan.view.camera.CameraUtils
import com.budi.setiawan.storyappbudisetiawan.view.main.MainActivity
import com.budi.setiawan.storyappbudisetiawan.view.maps.MapsFragment
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var user: UserItems
    private var latLng: LatLng? = null

    private val viewModel: CreateViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkAllPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                MainActivity.REQUIRED_PERMISSIONS,
                MainActivity.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (currentFile != null) binding.imagePreview.setImageURI(Uri.fromFile(currentFile))
        if (latLng != null) binding.tvLocation.text =
            getString(R.string.maps_lat_lon_format, latLng!!.latitude, latLng!!.longitude)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction()
        setupViewModel()
    }

    private fun setupAction() {
        binding.uploadButton.setOnClickListener {
            uploadFile()
        }

        binding.textBtnLocation.setOnClickListener {
            findNavController().navigate(
                CreateFragmentDirections.actionCreateFragmentToMapsFragment(
                    MapsFragment.ACTION_PICK_LOCATION
                )
            )
        }

        setFragmentResultListener(MapsFragment.KEY_RESULT) { _, bundle ->
            val location = bundle.getParcelable(MapsFragment.KEY_LAT_LONG) as LatLng?
            if (location != null) {
                binding.tvLocation.text =
                    getString(R.string.maps_lat_lon_format, location.latitude, location.longitude)
                latLng = location
            }
        }

        binding.btnCamera.setOnClickListener {
            startCameraX()
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }
    }

    private fun setupViewModel() {
        viewModel.userItem.observe(viewLifecycleOwner) { userItem ->
            this.user = userItem
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { state ->
            showLoading(state)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            ToastError.showToast(requireContext(), message)
        }

        viewModel.isSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                findNavController().navigateUp()
            }
        }
    }

    private fun uploadFile() {
        val description = binding.edtDesc.text.toString()

        if (description.isEmpty()) {
            binding.edtDesc.error = getString(R.string.dont_be_empty)
            return
        }

        if (currentFile == null) {
            ToastError.showToast(requireContext(), getString(R.string.select_a_picture))
            return
        }

        if (currentFile != null) {
            val file = CameraUtils.reduceFileImage(currentFile as File)

            val desc = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewModel.uploadImage(user.token ?: "", imageMultipart, desc, latLng)

        } else {
            ToastError.showToast(requireContext(), getString(R.string.select_a_picture))
        }
    }

    private fun checkAllPermissionsGranted() = MainActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity().baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        launcherIntentCameraX.launch(Intent(requireContext(), CameraActivity::class.java))
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        launcherIntentGallery.launch(Intent.createChooser(intent, "Choose a picture!"))
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                currentFile = CameraUtils.uriToFile(it.data?.data as Uri, requireContext())
                binding.imagePreview.setImageURI(Uri.fromFile(currentFile))
            }
        }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == MainActivity.CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result =
                CameraUtils.rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)

            val os: OutputStream = BufferedOutputStream(FileOutputStream(myFile))
            result.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.close()

            currentFile = myFile

            binding.imagePreview.setImageBitmap(result)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}