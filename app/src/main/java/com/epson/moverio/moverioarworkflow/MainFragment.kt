package com.epson.moverio.moverioarworkflow


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class MainFragment : Fragment() {

    /*
            override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

        }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val buttonAdjustment: Button = view.findViewById(R.id.btnAdjustment)

        buttonAdjustment.setOnClickListener {

            val intent = Intent(requireContext(), AdjustmentActivity::class.java)
            startActivity(intent)
        }

        val buttonStart: Button = view.findViewById(R.id.btnStart)

        buttonStart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                ?.replace(R.id.fragmentContainerView, MoverioIntroFragment())
                ?.addToBackStack("fragment_main") // This adds the transaction to the back stack
                ?.commit()
        }

        return view
    }
}