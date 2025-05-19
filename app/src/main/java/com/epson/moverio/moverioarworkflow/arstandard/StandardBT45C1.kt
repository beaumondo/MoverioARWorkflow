package com.epson.moverio.moverioarworkflow.arstandard

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.epson.moverio.moverioarworkflow.R

/**
 * Standard Experience BT-45C Step 1 - Product Video
 *
 * Manages the fragment layer for the standard experience
 * This first step shows the specifications and has buttons to move back and next
 *
 *  * Author: Giles Edward Beaumont
 *  * Date: 08/01/2025
 */
class StandardBT45C1 : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_standard_bt45c_1, container, false)

        //Back button and listener to return to Main Activity
        val backButton = view.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        //Next button to move forward to next page in standard experience
        val btnNext: Button = view.findViewById(R.id.btnNext)
        btnNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, StandardBT45C2())
                ?.addToBackStack("fragment_standard_bt45c_1")
                ?.commit()
        }

        //Epson: Create reference to video view and pass uri of mp4 video from assets/raw
        val videoView = view.findViewById<VideoView>(R.id.videoViewBT45Features).apply {
            setVideoURI(Uri.parse("android.resource://com.epson.moverio.moverioarworkflow/${R.raw.bt45c_features}"))
            val mediaController = MediaController(context).apply {
                setAnchorView(this@apply)
            }
            setMediaController(mediaController)

            start()
        }

        // Inflate the layout for this fragment
        return view


    }
}