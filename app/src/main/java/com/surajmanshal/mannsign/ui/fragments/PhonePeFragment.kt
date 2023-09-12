package com.surajmanshal.mannsign.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
    lateinit var paymentActivity : PaymentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentActivity = requireActivity() as PaymentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val loadingDialog = paymentActivity
            .loadingScreen
            .loadingScreen("Initiating Payment")
        val upiApps = getInstalledUPIApps()/*listOf("Paytm","PhonePe","GPay","Bhim")*/
        binding = FragmentPhonePeBinding.inflate(layoutInflater)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, upiApps)

        // Set the adapter to the ListView
        binding.upiAppsList.setAdapter(adapter)
        binding.upiAppsList.setOnItemClickListener { adapterView, view, i, l ->
            val appPkg = upiApps[i]
            loadingDialog.show()
            paymentActivity.initiatePayment(appPkg){
                loadingDialog.dismiss()
            }
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

    private fun getInstalledUPIApps(): ArrayList<String> {
        val upiList = ArrayList<String>()
        val uri = Uri.parse("upi://pay")
        val upiUriIntent = Intent().apply { data = uri }
        val packageManager = requireActivity().application.packageManager
        val resolveInfoList =
            packageManager.queryIntentActivities(upiUriIntent, PackageManager.MATCH_DEFAULT_ONLY)
        resolveInfoList.forEach { resolveInfo ->
            upiList.add(resolveInfo.activityInfo.packageName)
        }
        return upiList
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
