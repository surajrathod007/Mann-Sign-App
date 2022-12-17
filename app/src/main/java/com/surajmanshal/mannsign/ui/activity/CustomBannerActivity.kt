package com.surajmanshal.mannsign.ui.activity

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.surajmanshal.mannsign.databinding.ActivityCustomBannerBinding
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.URIPathHelper
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class CustomBannerActivity : AppCompatActivity() {

    private val maskDragMessage = "Mask Added"
    private val maskOn = "Bingo! Mask On"
    private val maskOff = "Mask off"

    var mHeight = 100f
    var mWidth = 100f

    val REQUEST_CODE = 0

    lateinit var binding: ActivityCustomBannerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomBannerBinding.inflate(layoutInflater)


        binding.llMain.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.btnApplyImage.setOnClickListener {
            //onApply()
            calculateMeasures()
        }
        binding.customPosterImage.setOnClickListener {
            chooseImage()
        }


        editTextWatchers()
        setContentView(binding.root)
    }

    fun onApply() {
        val params = binding.customPosterImage.layoutParams
        params.height = pxToDp(binding.edHeight.text.toString().toFloat())
        params.width = pxToDp(binding.edWidth.text.toString().toFloat())

        binding.customPosterImage.layoutParams = params

    }

    fun editTextWatchers(){
        binding.edWidth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if(!text.isNullOrEmpty())
//                    calculateMeasures()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.edHeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if(!text.isNullOrEmpty())
//                    calculateMeasures()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }


    fun calculateMeasures(){

        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)

        val wManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val density = resources.displayMetrics.density
        val displayWidth = display.widthPixels/density

        val AREA = displayWidth

        val height = binding.edHeight.text.toString().toFloat()
        val width = binding.edWidth.text.toString().toFloat()

        var newHeight = 0f
        var newWidth = 0f

        if(height>width){
            newHeight = AREA.toFloat()
            newWidth = (min(height,width)*AREA)/max(height,width)
        }else if(height<width){
            newWidth = AREA.toFloat()
            newHeight = (min(height,width)*AREA)/max(height,width)
        }else{
            newWidth = AREA.toFloat()
            newHeight = AREA.toFloat()
        }

        val params = binding.customPosterImage.layoutParams
        params.height = pxToDp(newHeight)
        params.width = pxToDp(newWidth)

        binding.customPosterImage.layoutParams = params

    }

    private fun Context.pxToDp(value : Float) : Int{
        val r : Resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,value,r.displayMetrics
        ).roundToInt()
    }

    fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode==REQUEST_CODE){
            //you got the image
            var uri = data?.data
            if(uri != null){
                setImageHeightWidth(this@CustomBannerActivity,uri)
            }
        }
    }

    fun setImageHeightWidth(c : Context,uri : Uri?){

        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)

        val wManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val density = resources.displayMetrics.density
        val displayWidth = display.widthPixels/density

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(uri?.let { URIPathHelper().getPath(c, it) },options)
        //Functions.makeToast(c,"Height = ${options.outHeight} Width = ${options.outWidth}")
        val width = options.outWidth.toFloat()
        val height = options.outHeight.toFloat()

        binding.edHeight.setText(height.toInt().toString())
        binding.edWidth.setText(width.toInt().toString())


        Functions.makeToast(c,"Total width is $displayWidth",true)

        val AREA = displayWidth
        var newHeight = 0f
        var newWidth = 0f

        if(height>width){
            newHeight = AREA.toFloat()
            newWidth = (min(height,width)*AREA)/max(height,width)
        }else if(height<width){
            newWidth = AREA.toFloat()
            newHeight = (min(height,width)*AREA)/max(height,width)
        }else{
            newWidth = AREA.toFloat()
            newHeight = AREA.toFloat()
        }

        val params = binding.customPosterImage.layoutParams
        params.height = pxToDp(newHeight)
        params.width = pxToDp(newWidth)

        binding.customPosterImage.layoutParams = params
        binding.customPosterImage.setImageURI(uri)
    }
}









