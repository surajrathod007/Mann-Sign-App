package com.surajmanshal.mannsign.ui.activity

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.databinding.ActivityCustomBannerBinding

class CustomBannerActivity : AppCompatActivity() {

    private val maskDragMessage = "Mask Added"
    private val maskOn = "Bingo! Mask On"
    private val maskOff = "Mask off"

    lateinit var binding : ActivityCustomBannerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomBannerBinding.inflate(layoutInflater)

        attachViewDragListener()
        setContentView(binding.root)
    }

    private fun attachViewDragListener(){
        binding.txtDragMe.setOnLongClickListener { view : View ->

            val item = ClipData.Item(maskDragMessage)

            val dataToDrag = ClipData(maskDragMessage, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),item)

            val maskShadow = View.DragShadowBuilder(view)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                //support pre-Nougat versions
                @Suppress("DEPRECATION")
                view.startDrag(dataToDrag, maskShadow, view, 0)
            } else {
                //supports Nougat and beyond
                view.startDragAndDrop(dataToDrag, maskShadow, view, 0)
            }

            //view.visibility = View.INVISIBLE

            true
        }
    }



}



