package com.example.a26apigooglemap.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.a26apigooglemap.GAccount
import com.example.a26apigooglemap.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var animator: ObjectAnimator

    @Inject
    lateinit var account: GAccount
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animationButton()
        binding.btSignIn.setOnClickListener {
            requireActivity().startActivityForResult(account.getSingInIntent(), 123)
            parentFragmentManager.popBackStack()
        }
    }

    private fun animationButton() {
        animator = ObjectAnimator.ofFloat(binding.btSignIn, View.ALPHA, 0.8f, 1f)
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.duration = 500
        animator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        animator.cancel()
    }
}