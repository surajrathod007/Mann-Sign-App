package com.surajmanshal.mannsign.ui.activity

import android.app.DownloadManager
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.FontAdapter
import com.surajmanshal.mannsign.databinding.ActivityCustomAcpBoardBinding
import java.io.File

class CustomAcpBoardActivity : AppCompatActivity() {
    lateinit var binding : ActivityCustomAcpBoardBinding
    lateinit var downLoadManager : DownloadManager
    val fileNames = listOf("font1.ttf","font2.ttf")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomAcpBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        downLoadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        setupRecyclerView()

        val fontUrls = arrayListOf<String>("https://fontsfree.net//wp-content/fonts/basic/sans-serif/dDihapus404.ttf","https://fontsfree.net//wp-content/fonts/basic/sans-serif/FontsFree-Net-ALSDirect2.ttf")

        binding.btnDownload.setOnClickListener {
            var c = 1
            if(checkFiles() != 2){
                try{
                    clearDownloads()
                    fontUrls.forEach {
                        downloadFile(it,"font$c.ttf")
                        c++
                    }
                }catch (e : Exception){
                    Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Files already downloaded",Toast.LENGTH_LONG).show()
            }
        }

        binding.btnApplyFont.setOnClickListener {
            try{
                applyFont("font1.ttf")
            }catch (e : Exception){
                Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
            }
        }

        binding.edName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.rvFonts.adapter = FontAdapter(this@CustomAcpBoardActivity,fileNames,text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })





    }

    private fun downloadFile(url : String, filename : String){
        val request = DownloadManager.Request(Uri.parse(url))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDescription("Please wait...")
        request.setTitle("Font is downloading")
        request.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,filename)
        downLoadManager.enqueue(request)
    }

    private fun clearDownloads(){
        //to get all files in download folder
        var f = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"/")
        f.listFiles().forEach {
            it.delete()
        }
        Toast.makeText(this,"Download is cleared ",Toast.LENGTH_LONG).show()
    }

    private fun checkFiles() : Int{
        val f = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"/")
        return f.listFiles().size
    }

    private fun applyFont(fontName : String){
        var f = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"/")
        var myFont = f.listFiles().filter { it.name == fontName }
    }

    private fun setupRecyclerView(){
        binding.rvFonts.adapter = FontAdapter(this,fileNames,"Sample Text")
    }


}