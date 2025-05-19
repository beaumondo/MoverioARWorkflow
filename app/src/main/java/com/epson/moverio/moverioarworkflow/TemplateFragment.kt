package com.epson.moverio.moverioarworkflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * Title
 *
 * Description of what the underlying fragment does.
 *
 *  * Author: Giles Beaumont
 *  * Date:
 */
class TemplateFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_template, container, false)

        //Back button and listener to return to Main Activity
        val backButton = view.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        // Inflate the layout for this fragment
        return view


    }
}