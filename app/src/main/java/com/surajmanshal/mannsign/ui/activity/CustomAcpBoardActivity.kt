package com.surajmanshal.mannsign.ui.activity

import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.SecuredScreenActivity
import com.surajmanshal.mannsign.adapter.recyclerview.FontAdapter
import com.surajmanshal.mannsign.databinding.ActivityCustomAcpBoardBinding
import com.surajmanshal.mannsign.viewmodel.CustomAcpViewModel
import java.io.File

class CustomAcpBoardActivity : SecuredScreenActivity() {

    lateinit var binding: ActivityCustomAcpBoardBinding
    lateinit var downLoadManager: DownloadManager
    lateinit var vm: CustomAcpViewModel
    lateinit var bottomSheet: BottomSheetDialog
    lateinit var colorPickerDialog: ColorPickerDialog

    val fileNames = listOf("font1.ttf", "font2.ttf")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomAcpBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        downLoadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        vm = ViewModelProvider(this).get(CustomAcpViewModel::class.java)

        window.statusBarColor = Color.BLACK
        //todo : fetch links from database
        val fontUrls = arrayListOf<String>(
            "https://fontsfree.net//wp-content/fonts/basic/sans-serif/dDihapus404.ttf",
            "https://fontsfree.net//wp-content/fonts/basic/sans-serif/FontsFree-Net-ALSDirect2.ttf"
        )

        binding.btnDownload.setOnClickListener {
            var c = 1
            if (checkFiles() != 2) {
                try {
                    clearDownloads()
                    fontUrls.forEach {
                        downloadFile(it, "font$c.ttf")
                        c++
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Files already downloaded", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnApplyFont.setOnClickListener {
            try {
                showBottomSheet()
            } catch (e: Exception) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
            }
        }


        binding.edName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                vm.setCurrentName(text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.btnApplyColor.setOnClickListener {
            showColorPicker()
        }


        setupObservers()


    }

    private fun downloadFile(url: String, filename: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDescription("Please wait...")
        request.setTitle("Font is downloading")
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, filename)
        downLoadManager.enqueue(request)
    }

    private fun clearDownloads() {
        //to get all files in download folder
        var f = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/")
        f.listFiles().forEach {
            it.delete()
        }
        Toast.makeText(this, "Download is cleared ", Toast.LENGTH_LONG).show()
    }

    private fun checkFiles(): Int {
        val f = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/")
        return f.listFiles().size
    }

    private fun applyFont(fontName: String) {
        var f = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/")
        var myFont = f.listFiles().filter { it.name == fontName }
    }

    private fun setupRecyclerView() {
        //binding.rvFonts.adapter = FontAdapter(this,fileNames,"Sample Text")
    }


    private fun setupObservers() {
        vm.currentName.observe(this) {
            binding.txtSampleFontNameNew.text = it
        }
        vm.currentFontName.observe(this) {
            if (it != "") {
                val f = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/$it")
                binding.txtSampleFontNameNew.typeface = Typeface.createFromFile(f)
            }
        }
    }

    private fun showBottomSheet() {
        bottomSheet = BottomSheetDialog(this, R.style.BottomSheetTheme)

        val sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_acp, null)
        val rvFont = sheetView.findViewById<RecyclerView>(R.id.rvFontBottomSheet)
        val btnApplyFont = sheetView.findViewById<Button>(R.id.btnApplyFontBottomSheet)
        rvFont.adapter = FontAdapter(this, fileNames, vm.currentName.value.toString(), vm)
        btnApplyFont.setOnClickListener {
            bottomSheet.dismiss()
        }
        bottomSheet.setContentView(sheetView)
        bottomSheet.show()
    }

    private fun showColorPicker() {
        val b = ColorPickerDialog.Builder(this).setTitle("Choose Color")
            .setPositiveButton("Apply", object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    binding.txtSampleFontNameNew.setTextColor(envelope!!.color)
                }
            }).setNegativeButton("Cancel"){v,m->
                v.dismiss()
            }
            .attachAlphaSlideBar(true) // the default value is true.
            .attachBrightnessSlideBar(true)  // the default value is true.
            .setBottomSpace(12)
        b.show()
    }
}