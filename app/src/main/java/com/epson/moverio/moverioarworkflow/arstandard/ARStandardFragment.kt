package com.epson.moverio.moverioarworkflow.arstandard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.epson.moverio.moverioarworkflow.R

/**
 * Title
 *
 * Description of what the underlying fragment does.
 *
 *  * Author: Giles Edward Beaumont
 *  * Date:
 */
class ARStandardFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_ar_standard_experience, container, false)

        //Back button and listener to return to Main Activity
        val backButton = view.findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        //BT-45C button event
        val buttonBT45C: Button = view.findViewById(R.id.btnBT45)
        buttonBT45C.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, StandardBT45C1())
                ?.addToBackStack("fragment_ar_standard_experience") // This adds the transaction to the back stack
                ?.commit()
        }

        //BT-40 button event
        val buttonBT40: Button = view.findViewById(R.id.btnBT40)
        buttonBT40.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, StandardBT401())
                ?.addToBackStack("fragment_ar_standard_experience")
                ?.commit()
        }

        // Inflate the layout for this fragment
        return view


    }
}