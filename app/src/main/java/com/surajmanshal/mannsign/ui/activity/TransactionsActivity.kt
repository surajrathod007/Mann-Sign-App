package com.surajmanshal.mannsign.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.surajmanshal.mannsign.AuthenticationActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.TransactionAdapter
import com.surajmanshal.mannsign.data.model.DateFilter
import com.surajmanshal.mannsign.databinding.ActivityTransactionsBinding
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.viewmodel.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionsActivity : AppCompatActivity() {

    lateinit var binding : ActivityTransactionsBinding
    lateinit var vm : TransactionViewModel
    lateinit var bottomSheetDialog: BottomSheetDialog
    var email : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)

        vm = ViewModelProvider(this).get(TransactionViewModel::class.java)

        setContentView(binding.root)
        val sharedPreference = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreference.getString("email", "")
        if (!email.isNullOrEmpty())
            loadTransactions(email!!)

        binding.shimmerTransactions.startShimmer()


        setupSpinner()
        setObserver()

        binding.btnTransactionBack.setOnClickListener {
            finish()
        }

        binding.loginRegisterTransaction.btnLoginRegister.setOnClickListener {
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }
        binding.spTransactions.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    when (position) {
                        0 -> {
                            Toast.makeText(
                                this@TransactionsActivity,
                                "Fetch All Transactions",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.getUserAllTransactions(email!!)
                            }
                        }
                        1 -> {
                            val d = DateFilter(LocalDate.now().minusDays(7), LocalDate.now())
                            Toast.makeText(
                                this@TransactionsActivity,
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.getUserTransactions(email!!,d)
                            }
                        }
                        2 -> {
                            val d = DateFilter(LocalDate.now().minusDays(30), LocalDate.now())
                            Toast.makeText(
                                this@TransactionsActivity,
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.getUserTransactions(email!!,d)
                            }
                        }
                        3 -> {
                            val d = DateFilter(LocalDate.now().minusDays(180), LocalDate.now())
                            Toast.makeText(
                                this@TransactionsActivity,
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.getUserTransactions(email!!,d)
                            }
                        }
                        4 -> {
                            val d = DateFilter(LocalDate.now().minusDays(365), LocalDate.now())
                            Toast.makeText(
                                this@TransactionsActivity,
                                "$d",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.getUserTransactions(email!!,d)
                            }
                        }
                        5 -> {
                            //open dialog
                            showBottomSheet(email!!)
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }



    }

    private fun loadTransactions(email : String){
        vm.getUserAllTransactions(email)
    }

    private fun setupSpinner() {

        val all = "Lifetime"

        val last7Days = "Last 7 days (${
            LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val last30Days = "Last 30 days (${
            LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val last180Days = "Last 180 days (${
            LocalDate.now().minusDays(180).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val last365Days = "Last 365 days (${
            LocalDate.now().minusDays(365).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))})"

        val custom = "Custom Date"

        val arr = arrayOf(all, last7Days, last30Days, last180Days, last365Days, custom)

        val adp = ArrayAdapter(this, R.layout.custom_spinner_view, arr)
        binding.spTransactions.adapter = adp

    }

    private fun showBottomSheet(email: String) {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val sheetView =
            LayoutInflater.from(this).inflate(R.layout.bottomsheet_datepicker, null)

        val startDate = sheetView.findViewById<DatePicker>(R.id.datePickerStart)
        val endDate = sheetView.findViewById<DatePicker>(R.id.datePickerEnd)
        val sheet = sheetView.findViewById<MaterialButton>(R.id.btnFilterDate)

        sheet.setOnClickListener {
            val s = LocalDate.of(startDate.year,startDate.month+1,startDate.dayOfMonth)
            val e = LocalDate.of(endDate.year,endDate.month+1,endDate.dayOfMonth)
            val d = DateFilter(s,e)
            //Toast.makeText(requireContext(),"$d",Toast.LENGTH_LONG).show()
            vm.getUserTransactions(email,d)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
    }

    private fun setObserver(){
        vm.msg.observe(this){
            Functions.makeToast(this@TransactionsActivity,it)
        }
        vm.transactionItems.observe(this){
            binding.rvTransactions.adapter = TransactionAdapter(it)
        }
        vm.isLoading.observe(this){
            if(!email.isNullOrEmpty()){
                if(it){
                    binding.shimmerTransactions.visibility = View.VISIBLE
                    binding.rvTransactions.visibility = View.GONE
                }else{
                    Handler().postDelayed({
                        binding.shimmerTransactions.visibility = View.GONE
                        binding.rvTransactions.visibility = View.VISIBLE
                    },1500)
                }
            }else{
                binding.shimmerTransactions.visibility = View.GONE
                binding.rvTransactions.visibility = View.GONE
                binding.bounceTransactionScroll.visibility = View.GONE
                binding.loginRegisterTransaction.root.visibility = View.VISIBLE
            }

        }
    }
}
