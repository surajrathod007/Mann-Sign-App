package com.surajmanshal.mannsign.ui.activity

import android.app.ActionBar
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.util.LayoutDirection
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.databinding.ActivityCustomBannerBinding
import com.surajmanshal.mannsign.utils.Functions

class CustomBannerActivity : AppCompatActivity() {

    private val maskDragMessage = "Mask Added"
    private val maskOn = "Bingo! Mask On"
    private val maskOff = "Mask off"

    lateinit var binding: ActivityCustomBannerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomBannerBinding.inflate(layoutInflater)

        binding.framLayout.setOnDragListener(dragListner)
        attachViewDragListener()
        setContentView(binding.root)
    }

    private fun attachViewDragListener() {
        binding.txtDragMe.setOnLongClickListener { view: View ->

            val item = ClipData.Item(maskDragMessage)

            val dataToDrag =
                ClipData(maskDragMessage, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)

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

    val dragListner = View.OnDragListener { view, dragEvent ->

        var xAxis = dragEvent.x
        var yAxis = dragEvent.y

        when(dragEvent.action){
            DragEvent.ACTION_DRAG_STARTED->{

                //dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                true
            }
            DragEvent.ACTION_DRAG_ENTERED ->{
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION->{
                true
            }
            DragEvent.ACTION_DRAG_EXITED->{
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP->{
                val item = dragEvent.clipData.getItemAt(0)
                val dragData = item.text
                Functions.makeToast(this@CustomBannerActivity,dragData.toString())

                view.invalidate()
                var v = dragEvent.localState as TextView
                val parent = v.parent as ViewGroup
                parent.removeView(v)


                val l = ConstraintLayout.LayoutParams(v.width,v.height)

                v.layoutParams = l
                l.setMargins(50)
                val destination = view as ConstraintLayout
                v.invalidate()
                destination.addView(v)
                v.invalidate()

                v.visibility = View.VISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED->{
                //view.invalidate()
                binding.txtPosition.text = xAxis.toString() + " " + yAxis.toString()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION->{
                xAxis = dragEvent.x
                yAxis = dragEvent.y
                binding.txtPosition.text = xAxis.toString() + " " + yAxis.toString()

                true
            }
            else -> {
                false
            }
        }
    }

}









