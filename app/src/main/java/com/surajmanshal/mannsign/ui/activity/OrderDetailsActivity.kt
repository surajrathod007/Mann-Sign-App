package com.surajmanshal.mannsign.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.OrderItemsAdapter
import com.surajmanshal.mannsign.databinding.ActivityOrderDetailsBinding
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.Functions.makeToast
import com.surajmanshal.mannsign.viewmodel.OrdersViewModel
import kotlinx.coroutines.Runnable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class OrderDetailsActivity : AppCompatActivity() {

    private val requestCode: Int = 123
    lateinit var binding: ActivityOrderDetailsBinding
    lateinit var vm: OrdersViewModel

    lateinit var mHandler: Handler
    lateinit var mRunnable: Runnable

    var isRead = false
    var isWrite = false

    lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
//        if(mRunnable != null){
//            mHandler.removeCallbacks(mRunnable)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(OrdersViewModel::class.java)
        setContentView(binding.root)

        val id = intent.getStringExtra("id")


        window.statusBarColor = Color.BLACK
        if (!id.isNullOrEmpty()) {
            vm.getOrderById(id)
        } else {
            Functions.makeToast(this@OrderDetailsActivity, "Order id is null !")
        }
        //TODO : Every 5 second new request is made
        /*
        mHandler = Handler()
        mHandler.post(object : Runnable {
            override fun run() {
                mRunnable = this
                if (!id.isNullOrEmpty()) {
                    vm.getOrderById(id)
                }

                mHandler.postDelayed(this,5000)
            }
        })

         */


        setObservers()

        binding.btnOrderDetailBack.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.btnOrderChat.setOnClickListener {
            val i = Intent(this, ChatActivity::class.java)
            i.putExtra("id", id)
            startActivity(i)
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            isRead = it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isRead
            isWrite = it[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWrite
        }
        binding.btnDownloadInvoice.setOnClickListener {
            requestPermission()
            makeInvoice()
        }
        binding.btnMakePayment.setOnClickListener {
            val d = AlertDialog.Builder(this)
            d.setTitle("Want to make payment ?")
            d.setMessage("You can make payment through paytm app or in browser !")
            d.setPositiveButton("Yes"){d,w ->
                makePayment()
            }
            d.setNegativeButton("No"){d,w ->
                makeToast(this,"Payment canceled")
                d.dismiss()
            }
            d.show()
        }

    }

    private fun makePayment() {

        var callBackUrl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=${vm.order.value?.orderId}"
        var orderId = vm.order.value?.orderId.toString()
        val amount = vm.order.value?.total.toString()

        val txnToken = vm.getTransactionToken(orderId,vm.order.value!!.emailId,amount)
        if(txnToken != null){
            val paytmOrder = PaytmOrder(orderId,Constants.MERCHENT_ID,txnToken,amount,callBackUrl)
            val transaction = TransactionManager(paytmOrder,object : PaytmPaymentTransactionCallback {

                override fun onTransactionResponse(inResponse: Bundle?) {
                    Toast.makeText(applicationContext, "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();

                }

                override fun networkNotAvailable() {
                    makeToast(this@OrderDetailsActivity,"Network not available")
                }

                override fun onErrorProceed(p0: String?) {
                    makeToast(this@OrderDetailsActivity,"onErrorProceed $p0")
                }

                override fun clientAuthenticationFailed(p0: String?) {
                    makeToast(this@OrderDetailsActivity,"clientAuthenticationFailed $p0")
                }

                override fun someUIErrorOccurred(p0: String?) {
                    makeToast(this@OrderDetailsActivity,"someUIErrorOccurred $p0")
                }

                override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
                    makeToast(this@OrderDetailsActivity,"onErrorLoadingWebPage $p0 - $p1 - $p2")
                }

                override fun onBackPressedCancelTransaction() {
                    makeToast(this@OrderDetailsActivity,"onBackPressedCancelTransaction ")
                }

                override fun onTransactionCancel(p0: String?, p1: Bundle?) {
                    makeToast(this@OrderDetailsActivity,"onTransactionCancel $p0 - ${p1.toString()} ")
                }
            })
            transaction.setAppInvokeEnabled(true)
            transaction.setShowPaymentUrl("https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage")
            transaction.startTransaction(this, requestCode);
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == resultCode && data != null){
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show()
        }
    }
    private fun setObservers() {
        vm.order.observe(this) {
            binding.rvOrderItems.adapter = OrderItemsAdapter(this, it.orderItems!!)
            binding.txtOrderIdDetails.text = it.orderId
            binding.txtOrderDateDetails.text = it.orderDate.toString()
            binding.txtOrderTotalDetails.text = it.total.toString()
            binding.txtEstimatedDays.text = if (it.days == null) "0" else it.days.toString()
            if (!it.trackingUrl.isNullOrEmpty())
                binding.edTrackingUrl.setText(it.trackingUrl.toString())
            binding.txtOrderPaymentStatus.text = if (it.paymentStatus == 0) "Pending" else "Done"
            binding.txtOrderQuantityDetails.text = it.quantity.toString()
            binding.txtOrderDiscountDetails.text = "- ₹" + it.discount
            binding.txtOrderDeliveryDetails.text = "+ ₹" + it.deliveryCharge
            binding.txtOrderGrandTotalDetails.text = "₹" + it.totalRecieved
            if (it.paymentStatus == 0)
                binding.txtYouHaveToPay.text = "You have to pay : "
            else
                binding.txtYouHaveToPay.text = "You paid : "

            if (it.orderStatus == Constants.ORDER_CONFIRMED)
                binding.btnMakePayment.visibility = View.VISIBLE
            else
                binding.btnMakePayment.visibility = View.GONE

            if (it.orderStatus != Constants.ORDER_PENDING || it.orderStatus != Constants.ORDER_CONFIRMED || it.orderStatus != Constants.ORDER_CANCELED) {
                binding.btnDownloadInvoice.visibility = View.VISIBLE
            }else{
                binding.btnDownloadInvoice.visibility = View.GONE
            }
            when (it.orderStatus) {
                Constants.ORDER_PENDING -> {
                    binding.txtOrderDetailsStatus.text = "Pending"
                }
                Constants.ORDER_CONFIRMED -> {
                    binding.txtOrderDetailsStatus.text = "Confirmed"
                }
                Constants.ORDER_PROCCESSING -> {
                    binding.txtOrderDetailsStatus.text = "Processing"
                }
                Constants.ORDER_READY -> {
                    binding.txtOrderDetailsStatus.text = "Ready"
                }
                Constants.ORDER_OUT_FOR_DELIVERY -> {
                    binding.txtOrderDetailsStatus.text = "Out for delivery !"
                }
                Constants.ORDER_DELIVERED -> {
                    binding.txtOrderDetailsStatus.text = "Delivered"
                }
                Constants.ORDER_CANCELED -> {
                    binding.txtOrderDetailsStatus.text = "Canceled"
                }
            }
        }
        vm.isLoading.observe(this) {
            if (it) {
                binding.orderItemsLoading.visibility = View.VISIBLE
            } else {
                binding.orderItemsLoading.visibility = View.GONE
            }
        }
    }

    fun makeInvoice() {

        try {

            var lst = vm.order.value!!.orderItems

            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString()
            val file = File(path, "mann_sign_invoice${System.currentTimeMillis()}.pdf")
            val output = FileOutputStream(file)

            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)
            //document.setMargins(1f,1f,1f,1f)


            //header
            val headerImg = this.getDrawable(R.drawable.invoice_header)
            val bitmap = (headerImg as BitmapDrawable).bitmap
            val opstream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, opstream)
            val bitmapdata = opstream.toByteArray()

            val img = ImageDataFactory.create(bitmapdata)
            val myheader = Image(img)
            document.add(myheader)
            //first table
            val c: FloatArray = floatArrayOf(220F, 220F, 200F, 180F)
            val table1 = Table(c)
            // row 1
            table1.addCell(Cell().add(Paragraph("Bill to Party ").setFontSize(10.0f)).setBold())
            table1.addCell(Cell().add(Paragraph("Ship to Party ").setFontSize(10.0f)).setBold())
            table1.addCell(Cell().add(Paragraph("Date : ").setFontSize(10.0f)).setBold())
            table1.addCell(
                Cell().add(
                    Paragraph(LocalDate.now().toString()).setFontSize(10.0f).setBold()
                )
            )
            //row 2     //TODO : Fetch user address
            table1.addCell(Cell(4, 0).add(Paragraph("").setFontSize(8.0f)))
            table1.addCell(Cell(4, 0).add(Paragraph("").setFontSize(8.0f)))
            table1.addCell(Cell().add(Paragraph("Invoice No : ").setFontSize(10.0f)))
            table1.addCell(Cell().add(Paragraph("inv1000").setFontSize(10.0f)))

            //row 3
            //table1.addCell(Cell().add(Paragraph("")))
            //table1.addCell(Cell().add(Paragraph("")))
            table1.addCell(Cell().add(Paragraph("Buyer's Order No. :").setFontSize(10.0f)))
            table1.addCell(Cell().add(Paragraph(vm.order.value!!.orderId).setFontSize(10.0f)))

            //row 4
            //table1.addCell(Cell().add(Paragraph("")))
            //table1.addCell(Cell().add(Paragraph("")))
            table1.addCell(Cell().add(Paragraph("Order Date : ").setFontSize(10.0f)))
            table1.addCell(
                Cell().add(
                    Paragraph(
                        vm.order.value!!.orderDate.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy"))
                    ).setFontSize(10.0f)
                )
            )

            //row 5
            //table1.addCell(Cell().add(Paragraph("")))
            //table1.addCell(Cell().add(Paragraph("")))
            table1.addCell(Cell().add(Paragraph("State Code : ").setFontSize(10.0f)))
            table1.addCell(Cell().add(Paragraph("24").setFontSize(10.0f)))

            //row 6
            table1.addCell(Cell().add(Paragraph("State Code : ").setFontSize(8.0f).setBold()))
            table1.addCell(Cell().add(Paragraph("State Code : ").setFontSize(8.0f).setBold()))
            table1.addCell(
                Cell(0, 2).add(Paragraph("COMPANY GSTIN NO : ").setFontSize(8.0f).setBold())
                    .setTextAlignment(
                        TextAlignment.CENTER
                    )
            )
            //table1.addCell(Cell().add(Paragraph("")))

            //row 7
            table1.addCell(
                Cell().add(
                    Paragraph("GSTIN NO : <add here>").setFontSize(8.0f).setBold()
                )
            )
            table1.addCell(
                Cell().add(
                    Paragraph("GSTIN NO : <add here>").setFontSize(8.0f).setBold()
                )
            )
            table1.addCell(
                Cell(0, 2).add(Paragraph("24BENPP0006B1Z4").setFontSize(8.0f).setBold())
                    .setTextAlignment(
                        TextAlignment.CENTER
                    )
            )
            //table1.addCell(Cell().add(Paragraph("")))

            document.add(table1)

            //2nd table
            val table2 = Table(8)
            table2.useAllAvailableWidth()
            //row1
            table2.addCell(Cell().add(Paragraph("Sr No.").setBold().setFontSize(10.0f)))
            table2.addCell(
                Cell().add(
                    Paragraph("Product Description").setBold().setFontSize(10.0f)
                )
            )
            table2.addCell(Cell().add(Paragraph("HSN Code").setBold().setFontSize(10.0f)))
            table2.addCell(Cell().add(Paragraph("UOM").setBold().setFontSize(10.0f)))
            table2.addCell(Cell().add(Paragraph("Product Type").setBold().setFontSize(10.0f)))
            table2.addCell(Cell().add(Paragraph("Quantity").setBold().setFontSize(10.0f)))
            table2.addCell(Cell().add(Paragraph("Rate").setBold().setFontSize(10.0f)))
            table2.addCell(Cell().add(Paragraph("Amount").setBold().setFontSize(10.0f)))

            //add items
            var sr = 1
            var gtotal = 0.0f
            var extraRows = 15 - lst!!.size
            with(table2) {
                lst!!.forEach {
                    addCell(sr.toString()).setFontSize(10f)
                    addCell(it.product!!.posterDetails!!.title).setFontSize(10f)
                    addCell("Hsn$sr").setFontSize(10f)
                    addCell("UOM$sr").setFontSize(10f)
                    //set product type
                    if (it.product!!.posterDetails != null)
                        addCell("Poster").setFontSize(10f)
                    if (it.product!!.boardDetails != null)
                        addCell("ACP Board").setFontSize(10f)
                    if (it.product!!.bannerDetails != null)
                        addCell("Banner").setFontSize(10f)

                    addCell("${it.quantity}").setFontSize(10f)
                    addCell("${it.variant!!.variantPrice}").setFontSize(10f)
                    addCell("${it.totalPrice}").setFontSize(10f)
                    gtotal += it.totalPrice
                    sr++
                }

                for (i in 1..extraRows) {
                    addCell(sr.toString()).setFontSize(10f)
                    addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    addCell("").setFontSize(10f)
                    sr++
                }
            }

            val cgst = (gtotal * 9) / 100
            val sgst = (gtotal * 9) / 100

            document.add(table2)

            val col = floatArrayOf(200f, 200f, 200f, 180f)
            val table3 = Table(col)

            //bar
            val barImg = this.getDrawable(R.drawable.upi_barcode)
            val bitmap1 = (barImg as BitmapDrawable).bitmap
            val opstream1 = ByteArrayOutputStream()
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, opstream1)
            val bitmapdata1 = opstream1.toByteArray()

            var img1 = ImageDataFactory.create(bitmapdata1)

            var upibar = Image(img1)
            upibar.setHorizontalAlignment(HorizontalAlignment.CENTER)
            upibar.setHeight(80f)
            upibar.setWidth(80f)

            //row1
            table3.addCell(
                Cell().add(
                    Paragraph("Bank Detail").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Total Amount before Tax : ").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Rs. ${vm.order.value!!.total}").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row2
            table3.addCell(
                Cell().add(
                    Paragraph("Bank : Bank of Maharashtra").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.CENTER
                        )
                )
            )
            table3.addCell(
                Cell(5, 0).add(upibar).setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setVerticalAlignment(
                        VerticalAlignment.MIDDLE
                    )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Add: CGST (9%) : ").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Rs. $cgst").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row3
            table3.addCell(Cell().add(Paragraph("Bank A/C : 60207512779\n").setFontSize(10f)))
            //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
            table3.addCell(
                Cell().add(
                    Paragraph("Add: SGST (9%) : ").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Rs. $sgst").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row4
            table3.addCell(Cell().add(Paragraph("Bank IFSC : MAHB0001632").setFontSize(10f)))
            //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
            table3.addCell(
                Cell().add(
                    Paragraph("Total Tax Amount : ").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Rs. ${cgst + sgst}").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row5
            table3.addCell(Cell().add(Paragraph("PAN No. : BENPP0006B").setFontSize(10f)))
            //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
            table3.addCell(
                Cell().add(
                    Paragraph("Total Amount after Tax  ₹").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Rs. ${vm.order.value!!.total+cgst+sgst}").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )

            //row6
            table3.addCell(Cell().add(Paragraph("Mode of Transport : By Hand").setFontSize(10f)))
            //table3.addCell(Cell().add(Paragraph("Upi Payment").setFontSize(10f).setBold().setTextAlignment(TextAlignment.CENTER)))
            table3.addCell(
                Cell().add(
                    Paragraph("Delivery Charge : ").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Rs. ${vm.order.value!!.deliveryCharge}").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )

            //row7
            table3.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            table3.addCell(
                Cell().add(
                    Paragraph("UPI ID: 7405736990@okbizaxis").setFontSize(8f).setBold()
                        .setTextAlignment(
                            TextAlignment.CENTER
                        )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Grand Total : ").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.RIGHT
                    )
                )
            )
            table3.addCell(
                Cell().add(
                    Paragraph("Rs. ${vm.order.value!!.totalRecieved}").setFontSize(10f).setBold()
                        .setTextAlignment(
                            TextAlignment.RIGHT
                        )
                )
            )

            document.add(table3)

            //last table
            val table4 = Table(floatArrayOf(300f, 100f, 100f, 100f))

            //bar
            val stampImg = this.getDrawable(R.drawable.stamp)
            val bitmap2 = (stampImg as BitmapDrawable).bitmap
            val opstream2 = ByteArrayOutputStream()
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, opstream2)
            val bitmapdata2 = opstream2.toByteArray()

            var img2 = ImageDataFactory.create(bitmapdata2)
            var stamp = Image(img2)
            stamp.setHorizontalAlignment(HorizontalAlignment.CENTER)
            stamp.setHeight(80f)
            stamp.setWidth(80f)


            //row1
            table4.addCell(Cell(2, 0).add(Paragraph("Rupees :").setFontSize(10f)))
            table4.addCell(Cell(6, 0).add(Paragraph("  ").setFontSize(10f)))
            table4.addCell(Cell(6, 0).add(stamp))
            table4.addCell(
                Cell(5, 0).add(
                    Paragraph("for MANN SIGN:").setFontSize(10f).setBold().setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )

            //row2
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

            //row3
            table4.addCell(Cell().add(Paragraph("** E. & O. E.").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

            //row4
            table4.addCell(
                Cell(3, 0).add(
                    Paragraph("SUBJECT TO AHMEDABAD JURISDICTION\nThis is a Computer Generated Invoice").setFontSize(
                        10f
                    ).setTextAlignment(
                        TextAlignment.CENTER
                    )
                )
            )
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

            //row5
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))

            //row6
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("  ").setFontSize(10f)))
            //table4.addCell(Cell().add(Paragraph("Veh.No :").setFontSize(10f)))
            table4.addCell(
                Cell().add(
                    Paragraph("Authorised Signatory ").setVerticalAlignment(
                        VerticalAlignment.BOTTOM
                    ).setFontSize(8f).setTextAlignment(TextAlignment.CENTER)
                )
            ).setVerticalAlignment(
                VerticalAlignment.BOTTOM
            )



            document.add(table4)
            document.close()
            Toast.makeText(this, "Pdf Created", Toast.LENGTH_SHORT).show()
            openFile(file, path)

        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    fun openFile(file: File, path: String) {
        val intent = Intent(Intent.ACTION_VIEW)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val uri = FileProvider.getUriForFile(this, this.packageName + ".provider", file)
            intent.setData(uri)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } else {
            intent.setDataAndType(Uri.parse(path), "application/pdf")
            val i = Intent.createChooser(intent, "Open File With")
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        }
    }

    fun requestPermission(){

        //check permission already granted or not
        isRead = ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isWrite = ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        var permissionRequest : MutableList<String> = ArrayList()

        if(!isRead){
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if(!isWrite){
            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if(permissionRequest.isNotEmpty()){
            //request permission
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }

    }
}