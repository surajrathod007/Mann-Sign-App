package com.surajmanshal.mannsign.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.surajmanshal.mannsign.PaymentActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.IconedSpinnerAdapter
import com.surajmanshal.mannsign.data.model.payment.UPIApp
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
        val adapter =
            IconedSpinnerAdapter(requireContext(), upiApps.map {
                UPIApp(
                    getAppIcon(it),
                    getAppName(it),
                    pkg = it
                )
            })

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
            tvToolbarTitle.text = "Select UPI App"
            ivBackButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        return binding.root
    }

    private fun getAppName(pkg: String): String {
        return when(pkg){
            "com.google.android.apps.nbu.paisa.user" -> "GPAY"
            "net.one97.paytm" -> "Paytm"
            "com.phonepe.app" -> "Phonepe"
            "in.org.npci.upiapp" -> "BHIM"
            "in.amazon.mShop.android.shopping" -> "Amazon"
            "com.whatsapp" -> "WhatsApp"
            else -> pkg
        }
    }

    private fun getAppIcon(pkg: String): Int? {
        return when(pkg){
            "com.google.android.apps.nbu.paisa.user" -> R.drawable.gpay
            "net.one97.paytm" -> R.drawable.paytm
            "com.phonepe.app" -> R.drawable.phonepe
            "in.org.npci.upiapp" -> 5
            "in.amazon.mShop.android.shopping" -> R.drawable.amazone_pay
            "com.whatsapp" -> R.drawable.ic_wa
            else -> null
        }
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
