package com.epson.moverio.moverioarworkflow

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment

/**
 * Moverio Introduction Fragment
 *
 * Displays an introductory video on the Moverio history
 *
 *  * Author: Giles Beaumont
 *  * Date: 2024-11-26
 */
class MoverioIntroFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_moverio_intro, container, false)

        val btnNext: Button = view.findViewById(R.id.btnNext)
        btnNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                ?.replace(R.id.fragmentContainerView, SelectWorkflowFragment())
                ?.addToBackStack("fragment_moverio_intro") // This adds the transaction to the back stack
                ?.commit()
        }

        //Epson: Create reference to video view and pass uri of mp4 video from assets/raw
        val videoView = view.findViewById<VideoView>(R.id.videoViewMoverioHistory).apply {
            setVideoURI(Uri.parse("android.resource://com.epson.moverio.moverioarworkflow/${R.raw.moverio_history}"))
            val mediaController = MediaController(context).apply {
                setAnchorView(this@apply)
            }
            setMediaController(mediaController)

            start()
        }

        //Back button and listener to return to Main Activity
        val backButton = view.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Inflate the layout for this fragment
        return view
    }
}