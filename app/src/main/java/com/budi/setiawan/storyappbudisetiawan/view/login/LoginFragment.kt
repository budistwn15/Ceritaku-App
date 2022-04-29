package com.budi.setiawan.storyappbudisetiawan.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.budi.setiawan.storyappbudisetiawan.R
import com.budi.setiawan.storyappbudisetiawan.databinding.FragmentLoginBinding
import com.budi.setiawan.storyappbudisetiawan.error.ToastError
import com.budi.setiawan.storyappbudisetiawan.factory.ViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View{
        _binding = FragmentLoginBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel(){
        viewModel.userItem.observe(viewLifecycleOwner) { userModel ->
            if (userModel?.isLoggedIn == true) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            ToastError.showToast(requireContext(), message)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { state ->
            showLoading(state)
        }
    }

    private fun setupAction(){
        setButtonEnable()
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = "Masukkan password"
                }
                else -> {
                    viewModel.login(email, password)
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, login)
            startDelay = 500
        }.start()
    }

    private fun setButtonEnable() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        binding.loginButton.isEnabled = true && email.isNotEmpty() && true && password.isNotEmpty() && password.length >= 6
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}