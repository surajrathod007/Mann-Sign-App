package com.surajmanshal.mannsign.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.surajmanshal.mannsign.PaymentActivity
import com.surajmanshal.mannsign.databinding.FragmentPhonePeBinding
import com.surajmanshal.mannsign.utils.show


class PhonePeFragment : Fragment() {

    lateinit var binding: FragmentPhonePeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val parentActivity = (requireActivity() as PaymentActivity)
        val loadingDialog = parentActivity
            .loadingScreen
            .loadingScreen("Initiating Payment")
        val upiApps = listOf("Paytm","PhonePe","GPay","Bhim")
        binding = FragmentPhonePeBinding.inflate(layoutInflater)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, upiApps)

        // Set the adapter to the ListView
        binding.upiAppsList.setAdapter(adapter)
        binding.upiAppsList.setOnItemClickListener { adapterView, view, i, l ->
            val appPkg = upiApps[i]
            loadingDialog.show()
            // Todo : Make api call to get PayAPI RequestPayLoad
        }
        /*binding.optionUPI.setOnClickListener {
            binding.upiAppsList.toggleVisibility()
        }*/
        binding.upiAppsList.show()
        binding.payOptionsToolbar.apply {
            tvToolbarTitle.text = "Select Payment Method"
            ivBackButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            PhonePeFragment().apply {

            }
    }
}

private fun View.toggleVisibility() {
    isVisible = !isVisible
}
