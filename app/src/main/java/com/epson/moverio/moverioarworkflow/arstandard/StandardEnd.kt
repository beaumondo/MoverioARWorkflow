package com.epson.moverio.moverioarworkflow.arstandard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.epson.moverio.moverioarworkflow.MainFragment
import com.epson.moverio.moverioarworkflow.R
import com.epson.moverio.moverioarworkflow.SelectWorkflowFragment

/**
 * Standard Experience BT-45C Step End - End of the Moverio Standard Steps
 *
 * Manages the fragment layer for the standard experience last step
 * Provides options for the user to go back to the home page
 *
 *  * Author: Giles Edward Beaumont
 *  * Date: 10/01/2025
 */
class StandardEnd : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ar_end, container, false)

        //Back button and listener to return to Main Activity
        val backButton = view.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        //Home Page Button
        val buttonHome: Button = view.findViewById(R.id.btnHome)
        buttonHome.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, MainFragment())
                ?.commit()
        }

        //Go back to Select Workflow Page
        val buttonSelectExperience: Button = view.findViewById(R.id.btnSelectWorkflow)
        buttonSelectExperience.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SelectWorkflowFragment())
                ?.commit()
        }

        // Inflate the layout for this fragment
        return view
    }
}