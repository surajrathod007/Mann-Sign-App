package com.surajmanshal.mannsign.ui.activity

import android.animation.LayoutTransition
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.internal.LinkedTreeMap
import com.surajmanshal.mannsign.ImageUploading
import com.surajmanshal.mannsign.adapter.CustomSpinnerAdapter
import com.surajmanshal.mannsign.adapter.MaterialSpinnerAdapter
import com.surajmanshal.mannsign.data.model.Image
import com.surajmanshal.mannsign.data.model.Material
import com.surajmanshal.mannsign.data.model.Size
import com.surajmanshal.mannsign.data.model.product.ACPBoard
import com.surajmanshal.mannsign.data.model.product.Banner
import com.surajmanshal.mannsign.data.model.product.Poster
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.databinding.ActivityCustomBannerBinding
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.URIPathHelper
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import com.surajmanshal.mannsign.viewmodel.CustomBannerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class CustomBannerActivity : AppCompatActivity() {

    val arrProductType = listOf("Poster", "Banner")
    val REQUEST_CODE = 0
    var aspectRatio : String = ""

    lateinit var vm: CustomBannerViewModel
    lateinit var imageUploading : ImageUploading
    lateinit var d : LoadingScreen
    lateinit var dd : Dialog

    lateinit var binding: ActivityCustomBannerBinding

    var email : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomBannerBinding.inflate(layoutInflater)

        val sharedPreference =  getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreference.getString("email","No email")

        vm = ViewModelProvider(this)[CustomBannerViewModel::class.java]
        imageUploading = ImageUploading(this)
        d = LoadingScreen(this)
        dd = d.loadingScreen("Creating product")

        window.statusBarColor = Color.BLACK

        binding.llMain.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.btnApplyImage.setOnClickListener {
            //onApply()
            if (binding.edHeight.text.isNullOrEmpty() || binding.edWidth.text.isNullOrEmpty())
                Functions.makeToast(this, "Please enter dimensions")
            else
                calculateMeasures()
        }
        binding.customPosterImage.setOnClickListener {
            chooseImage()
        }
        binding.btnCustomBannerBack.setOnClickListener {
            finish()
        }
        binding.btnPlaceCustomOrder.setOnClickListener {
            uploadProductImage()
        }

        vm.getAllMaterials(Constants.TYPE_ALL)

        setupSpinner()
//        editTextWatchers() // No longer required
        setObservers()
        selectTypeListners()
        setContentView(binding.root)
    }

    fun onApply() {
        val params = binding.customPosterImage.layoutParams
        params.height = pxToDp(binding.edHeight.text.toString().toFloat())
        params.width = pxToDp(binding.edWidth.text.toString().toFloat())

        binding.customPosterImage.layoutParams = params

    }

    fun selectTypeListners() {
        binding.spCustomPosterType.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            fun setUpProductMaterials(productTypeId : Int){
                val productMaterials = vm.allMaterials.value?.filter { it.productTypeId == productTypeId }
                if (productMaterials != null) {
                    setupMaterialSpinner(productMaterials)
                }
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                vm.setProductType(
                    index,
                    Poster(title = "hii", "", null),
                    Banner(text = "Hello", font = 2)
                )
                when (index) {
                    0 -> {
                        binding.llPoster.visibility = View.VISIBLE
                        binding.llBanner.visibility = View.GONE
                    }
                    1 -> {
                        binding.llPoster.visibility = View.GONE
                        binding.llBanner.visibility = View.VISIBLE
                    }
                }
                with(Constants){
                    vm._currentProductTypeId.value =
                        when (index) {
                            0 -> {
                                setUpProductMaterials(TYPE_POSTER)
                                TYPE_POSTER
                            }
                            1 -> {
                                setUpProductMaterials(TYPE_BANNER)
                                TYPE_BANNER
                            }
                            2 -> TYPE_ACP_BOARD
                            else -> {
                                setUpProductMaterials(TYPE_POSTER)
                                TYPE_POSTER
                            }
                        }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
//                TODO("Not yet implemented")
            }
        }
    }

    fun setupMaterialSpinner(productMaterials : List<Material>){
        binding.spCustomMaterial.spinner.apply {
            setAdapter(
                MaterialSpinnerAdapter(this@CustomBannerActivity,productMaterials)
                /*ArrayAdapter(
                    this@CustomBannerActivity,
                    R.layout.support_simple_spinner_dropdown_item,
                    productMaterials.map { it.name }
                )*/
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                    vm.setMaterialId(productMaterials[index])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
//                    TODO("Not yet implemented")
                }

            }
            setSelection(0)
        }
    }

    fun setObservers() {
        vm.msg.observe(this) {
            Functions.makeToast(this, it.toString())
        }
        vm.allMaterials.observe(this){
            binding.spCustomMaterial.tvSpinnerName.text = "Material"
            val posterMaterials = it.filter { it.productTypeId == Constants.TYPE_POSTER }
            setupMaterialSpinner(posterMaterials)
        }
         with(vm){
             val owner = this@CustomBannerActivity
             serverResponse.observe(owner){
                 if(it.success) Toast.makeText(owner, it.message, Toast.LENGTH_SHORT).show()
             }
             imageUploading.imageUploadResponse.observe(owner){
                 if(it.success) {
//                     Toast.makeText(owner, it.message, Toast.LENGTH_SHORT).show()
                     val data = it.data as LinkedTreeMap<String,Any>

                     CoroutineScope(Dispatchers.IO).launch {
                         vm.addProduct(
                             createCustomProduct(Image(id = data["id"].toString().toDouble().toInt(), url = data["url"].toString(),
                                 description = data["description"].toString(),
                                 data["languageId"].toString().toFloat().toInt()
                             ))
                         )
                     }

                 }
                 else Log.d("Custom Order Image",it.message)
             }
             productUploadResponse.observe(owner){
//                 val variantId = it.variantId
//                 val productId = it.productId
//                 Toast.makeText(owner, "Product created pid$productId variantId$variantId", Toast.LENGTH_SHORT).show()
                 // Todo : Hide loading dialog and proceed to ordering process
                 val v = it
                 v.also {
                     it.variantPrice = getVariantPrice()
                 }
                 if(vm.orderPlaced!!.value!!){
                     Functions.makeToast(this@CustomBannerActivity,"Order already placed")
                 }else{
                     vm.addCustomOrder(variant = v, delivery = 0f, email = email!!)
                 }

                 dd.hide()
                 /*if(it.success)
                    try {
                        it.message.toInt()
                        Toast.makeText(owner, "Product created", Toast.LENGTH_SHORT).show()

                    }catch (e : java.lang.Exception){
                        Toast.makeText(owner, "id not received", Toast.LENGTH_SHORT).show()
                        Log.d("Custom Order Product",e.toString())
                    }
                 else Log.d("Custom Order Product",it.message)*/
             }
             _currentMaterial.observe(this@CustomBannerActivity){
                 binding.txtCustomOrderTotalPrice.text = "Your Product Price is : ${getVariantPrice()}"
             }
         }
    }

    private fun getVariantPrice(): Float {
        val s = createSize()
        return if(vm._currentMaterial.value != null){
            (s.width*s.width)* vm._currentMaterial.value!!.price
        }else{
            0.0f
        }
    }

    fun editTextWatchers() {
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

    fun gcd(a: Int, b: Int): Int {
        if (b == 0)
            return a
        return gcd(b, a % b)
    }

    fun calculateMeasures() {

        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)

        val wManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val density = resources.displayMetrics.density
        val displayWidth = display.widthPixels / density

        val AREA = displayWidth

        val height = binding.edHeight.text.toString().toFloat()
        val width = binding.edWidth.text.toString().toFloat()

        //aspect ratio setup
        val GCD = gcd(height.toInt(), width.toInt())
        binding.txtAspectRatioHint.text = "Current aspect ratio is ${(width/GCD).toInt()} : ${(height/GCD).toInt()} \nExpected ratio is $aspectRatio"

        var newHeight = 0f
        var newWidth = 0f

        if (height > width) {
            newHeight = AREA.toFloat()
            newWidth = (min(height, width) * AREA) / max(height, width)
        } else if (height < width) {
            newWidth = AREA.toFloat()
            newHeight = (min(height, width) * AREA) / max(height, width)
        } else {
            newWidth = AREA.toFloat()
            newHeight = AREA.toFloat()
        }

        val params = binding.customPosterImage.layoutParams
        params.height = pxToDp(newHeight)
        params.width = pxToDp(newWidth)

        binding.customPosterImage.layoutParams = params

    }

    private fun Context.pxToDp(value: Float): Int {
        val r: Resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value, r.displayMetrics
        ).roundToInt()
    }

    fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.CHOOSE_PRODUCT_IMAGE)
    }

    fun uploadProductImage(){
        CoroutineScope(Dispatchers.IO).launch {
            imageUploading.imageUri?.let {
                imageUploading.apply {
                    withContext(Dispatchers.Main){
                        dd.show()
                    }
                    // Todo : send proper language id
                    sendProductImage(createImageMultipart(),1)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.CHOOSE_PRODUCT_IMAGE) {
            //you got the image
            var uri = data?.data
            if (uri != null) {
                setImageHeightWidth(this@CustomBannerActivity, uri)
                imageUploading.imageUri = uri
            }
        }
    }

    fun setImageHeightWidth(c: Context, uri: Uri?) {

        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)

        val wManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val density = resources.displayMetrics.density
        val displayWidth = display.widthPixels / density

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(uri?.let { URIPathHelper().getPath(c, it) }, options)
        //Functions.makeToast(c,"Height = ${options.outHeight} Width = ${options.outWidth}")
        val width = options.outWidth.toFloat()
        val height = options.outHeight.toFloat()

        //binding.edHeight.setText(height.toInt().toString())
        //binding.edWidth.setText(width.toInt().toString())

        //Aspect ratio setup
        val GCD = gcd(width.toInt(), height.toInt())
        aspectRatio = "${(width/GCD).toInt()} : ${(height/GCD).toInt()}"
        binding.txtAspectRatio.text =
            "Aspect ratio is " + (width / GCD).toInt() + " : " + (height / GCD).toInt() + " (${width.toInt()} x ${height.toInt()})"

        Functions.makeToast(c, "Total width is $displayWidth", true)

        val AREA = displayWidth
        val newHeight: Float
        val newWidth: Float

        if (height > width) {
            newHeight = AREA.toFloat()
            newWidth = (min(height, width) * AREA) / max(height, width)
        } else if (height < width) {
            newWidth = AREA.toFloat()
            newHeight = (min(height, width) * AREA) / max(height, width)
        } else {
            newWidth = AREA.toFloat()
            newHeight = AREA.toFloat()
        }

        val params = binding.customPosterImage.layoutParams
        params.height = pxToDp(newHeight)
        params.width = pxToDp(newWidth)

        binding.customPosterImage.layoutParams = params
        binding.customPosterImage.setImageURI(uri)
    }

    fun setupSpinner() {
        //type spinner
        binding.spCustomPosterType.tvSpinnerName.text = "Product"
        binding.spCustomPosterType.spinner.apply {
            setAdapter(
                CustomSpinnerAdapter(this@CustomBannerActivity,arrProductType)
                /*ArrayAdapter(
                    this@CustomBannerActivity,
                    R.layout.support_simple_spinner_dropdown_item,
                    arrProductType
                )*/
            )
        }
    }

    fun createCustomProduct(image: Image): Product {
        val size = createSize()
        val materialId : Int = createMaterialId()
        val customProductSuffix =
            " of ${vm._currentMaterial.value?.name}(${size.width}x${size.height})"
        val languageId = 1
        val fontId = 1
        // Todo : Pricing details remaining (Product :BasePrice and Variant : variantPrice )
        return Product(0).apply {

            // Setting up product
            typeId = vm._currentProductTypeId.value
            images = listOf(image)
            sizes = listOf(size)
            materials = listOf(materialId)
            category = Constants.CATEGORY_CUSTOM_PRODUCT

            // Todo : below are dummy details replace them with user inputs
            languages = listOf(languageId)

            when(typeId){
                Constants.TYPE_POSTER -> {
                    posterDetails = Poster(
                        binding.edPosterTitle.text.toString(),
                        binding.edPosterDescription.text.toString(), null
                    )
                    subCategory = Constants.CATEGORY_CUSTOM_POSTER
                }
                Constants.TYPE_BANNER -> {
                    bannerDetails = Banner(
                        "My Banner$customProductSuffix", fontId
                    )
                    subCategory = Constants.CATEGORY_CUSTOM_BANNER
                }
                Constants.TYPE_ACP_BOARD -> {
                    boardDetails = ACPBoard(
                        "My Board$customProductSuffix",
                        0.5f,
                        "250-10-5",
                        fontId
                    )
                    subCategory = Constants.CATEGORY_CUSTOM_BOARD
                }
            }
        }
    }

    private fun createSize() = Size(
        0,
        binding.edWidth.text.toString().toInt(),
        binding.edHeight.text.toString().toInt()
    )
    private fun createMaterialId(): Int {
        return vm._currentMaterial.value?.id ?: vm.allMaterials.value?.get(0)?.id!!
    }

}