package com.unity.mynativeapp

import android.os.Bundle
import android.widget.Toast
import com.unity.mynativeapp.databinding.CustomLayoutBinding
import com.unity3d.player.UnityPlayerActivity

class OverlaidActivity : UnityPlayerActivity() {

    private var toast: Toast? = null
    private val binding by lazy { CustomLayoutBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUnityPlayer.addView(binding.root)
        setUpBottomNavigation()
    }

    private fun setUpBottomNavigation() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            toast?.cancel()

            toast = Toast.makeText(this, "${it.title} clicked!", Toast.LENGTH_LONG)
            toast?.show()
            true
        }
    }


}