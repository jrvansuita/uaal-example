package com.unity.mynativeapp

import android.os.Bundle
import com.unity.mynativeapp.databinding.CustomLayoutBinding
import com.unity3d.player.UnityPlayerActivity

class CustomUnityActivity : UnityPlayerActivity() {


    private val binding by lazy { CustomLayoutBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mUnityPlayer.addView(binding.root)
    }


}