package com.epson.moverio.moverioarworkflow.arstandard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.epson.moverio.moverioarworkflow.R

/**
 * Standard Experience BT-45C Step 2 - Product Features
 *
 * Manages the fragment layer for the standard experience step 2
 * This second step shows the specifications and features and has
 * buttons to move back and next
 *
 *  * Author: Giles Edward Beaumont
 *  * Date: 09/01/2025
 */
class StandardBT402 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_standard_bt40_2, container, false)

        //Back button and listener to return to Main Activity
        val backButton = view.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        //Next button to move forward to next page in standard experience
        val btnNext: Button = view.findViewById(R.id.btnNext)
        btnNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, StandardBT403())
                ?.addToBackStack("fragment_standard_bt40_2")
                ?.commit()
        }

        // Inflate the layout for this fragment
        return view


    }
}