package com.epson.moverio.moverioarworkflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.epson.moverio.moverioarworkflow.arimage.ARImageExperienceFragment
import com.epson.moverio.moverioarworkflow.arstandard.ARStandardFragment

/**
 * SelectWorkflowFragment
 *
 * Displays a list of workflows for the user to select and navigates to the corresponding details.
 *
 *  * Author: Edward Beaumont
 *  * Date: 2024-11-25
 */
class SelectWorkflowFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_select_workflow, container, false)

        //Back button and listener to return to Main Activity
        val backButton = view.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        //Standard Experience button event
        val buttonStandard: Button = view.findViewById(R.id.btnStandard)
        buttonStandard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                ?.replace(R.id.fragmentContainerView, ARStandardFragment())
                ?.addToBackStack("fragment_select_workflow") // This adds the transaction to the back stack
                ?.commit()
        }

        //AR Image Experience button event
        val buttonARImage: Button = view.findViewById(R.id.btnARImageExperience)
        buttonARImage.setOnClickListener {
            parentFragmentManager.beginTransaction()
                ?.replace(R.id.fragmentContainerView, ARImageExperienceFragment())
                ?.addToBackStack("fragment_select_workflow") // This adds the transaction to the back stack
                ?.commit()
        }

        //AR Image Experience button event
//        val buttonARObject: Button = view.findViewById(R.id.btnARObjectExperience)
//        buttonARObject.setOnClickListener {
//            Toast.makeText(
//                requireContext(),
//                "Object Detection is currently under development",
//                Toast.LENGTH_SHORT
//            ).show()
////            parentFragmentManager.beginTransaction()
////                ?.replace(R.id.fragmentContainerView, ARObjectExperienceFragment())
////                ?.addToBackStack("fragment_select_workflow") // This adds the transaction to the back stack
////                ?.commit()
//        }
        // Inflate the layout for this fragment
        return view


    }
}