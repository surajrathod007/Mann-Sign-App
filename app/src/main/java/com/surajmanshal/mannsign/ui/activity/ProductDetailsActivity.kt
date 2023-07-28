package com.surajmanshal.mannsign.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.CustomSpinnerAdapter
import com.surajmanshal.mannsign.adapter.MaterialSpinnerAdapter
import com.surajmanshal.mannsign.adapter.recyclerview.ReviewAdapter
import com.surajmanshal.mannsign.data.model.Review
import com.surajmanshal.mannsign.data.model.Variant
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.databinding.ActivityProductDetailsBinding
import com.surajmanshal.mannsign.databinding.AddReviewBottomSheetBinding
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.room.user.UserEntity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.Functions.makeToast
import com.surajmanshal.mannsign.utils.Functions.urlMaker
import com.surajmanshal.mannsign.utils.URIPathHelper
import com.surajmanshal.mannsign.utils.show
import com.surajmanshal.mannsign.utils.viewFullScreen
import com.surajmanshal.mannsign.viewmodel.CartViewModel
import com.surajmanshal.mannsign.viewmodel.ProductsViewModel
import com.surajmanshal.mannsign.viewmodel.ReviewsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var vm: ProductsViewModel
    private lateinit var cartVm: CartViewModel
    private lateinit var reviewViewModel: ReviewsViewModel
    private var currentUser: UserEntity? = null
    private lateinit var addReviewBottomSheetDialog: BottomSheetDialog
    var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this)[ProductsViewModel::class.java]
        cartVm = ViewModelProvider(this)[CartViewModel::class.java]
        reviewViewModel = ViewModelProvider(this)[ReviewsViewModel::class.java]
        window.statusBarColor = Color.BLACK
        val owner = this
        val sharedPreferences = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreferences.getString("email", Constants.NO_EMAIL)
        vm._currentProduct.value = intent.getSerializableExtra(Constants.PRODUCT) as Product?

        with(cartVm) {

            // Observers ----------------------------------------------------------------------------------

            _selectedVariant.observe(owner) {
                with(binding.productBuyingLayout) {
                    tvVariantPrice.text =
                        resources.getString(R.string.selected_variant_price) + it.variantPrice
                    tvAmount.text = "${it.variantPrice?.times(evQty.text.toString().toInt())}"
                }
                it.productId?.let { it1 -> setupButtonAction(email!!, it1) }
            }

            _selectedSize.observe(owner) {
                calculateVariantPrice()
            }
            _selectedMaterial.observe(owner) {
                calculateVariantPrice()
            }

            _currrentProductInCartVariants.observe(owner) {
                vm._currentProduct.value?.let { it1 -> setupButtonAction(email!!, it1.productId) }
            }

            cartVm.serverResponse.observe(owner) {
                Toast.makeText(owner, it.message, Toast.LENGTH_SHORT).show()
            }

        }
        with(vm) {

            // Observers ----------------------------------------------------------------------------------
            reviewResponse.observe(owner) {
                if (it.success) {
                    makeToast(this@ProductDetailsActivity, it.message)
                    _currentProduct.value?.let { it1 -> fetchProductReview(it1.productId) }
                    addReviewBottomSheetDialog.dismiss()
                }
            }

            _currentProductCategory.observe(owner, Observer {
                setupCategoryView(it.name)
            })
            _currentProductSubCategory.observe(owner, Observer {
                setupSubCategoryView(it.name)
            })
            _currentProductMaterial.observe(owner, Observer { materials ->
                mutableListOf<String>().apply {
                    materials.forEach {
                        add(it.name)
                        if (isNotEmpty()) binding.materialSpinner.spinner.setSelection(0)
                        if (size == materials.size) {
                            binding.materialSpinner.spinner.adapter =
                                MaterialSpinnerAdapter(this@ProductDetailsActivity, materials)
                            cartVm._selectedMaterial.value = materials[0]
                        }
                    }
//                    binding.materialSpinner.spinnerContainer.hint = "Material"
                    binding.materialSpinner.tvSpinnerName.text = "Material"
                }
            })
            _currentProductLanguage.observe(owner, Observer { languages ->
                mutableListOf<String>().apply {
                    languages.forEach {
                        add(it.name)
                        if (isNotEmpty()) binding.languageSpinner.spinner.setSelection(0)
                        if (size == languages.size) {
                            cartVm._selectedLanguage.value = languages[0]
                            setupSpinner(binding.languageSpinner.spinner, this)
                        }
                    }
                    binding.languageSpinner.tvSpinnerName.text = "Language"
                }
            })

            reviewViewModel.msg.observe(this@ProductDetailsActivity) {
                Toast.makeText(this@ProductDetailsActivity, it.toString(), Toast.LENGTH_SHORT)
                    .show()
                _currentProduct.value?.let { it1 -> fetchProductReview(it1.productId) }
            }
            reviewViewModel.selectedReview.observe(this@ProductDetailsActivity) {
                showReviewUpdateBottomSheet(this@ProductDetailsActivity, reviewViewModel).show()
            }

            _currentProductReviews.observe(owner) {
                setupProductReviews(it)
            }

            _currentProduct.value?.let { product ->
                with(product) {
                    cartVm._selectedVariant.value?.apply {
                        this.productId = product.productId
                        sizeId = sizes?.get(0)?.sid
                        materialId = materials?.get(0)
                        languageId = languages?.get(0)
                        cartVm.refreshVariant()
                    }
                    /*materials?.forEach { materialId -> getMaterialById(materialId) }
                    languages?.forEach { languageId -> getLanguageById(languageId) }*/
                    materials?.let { getMaterialsByIds(it) }
                    languages?.let { getLanguagesByIds(it) }
                    category?.let { categoryId -> getCategoryById(categoryId) }
                    subCategory?.let { subCategoryId -> getSubCategoryById(subCategoryId) }
                    fetchProductReview(productId)
                    // Check for review allowed or not

                    email?.let { mail ->
                        canReview(mail, productId) {
                            if (it) allowReview(mail, productId)
                        }
                    }

                }
                with(binding) {

                    if (product.images?.isNotEmpty() == true) {

                        val g = Glide.with(this@ProductDetailsActivity).asBitmap()
                            .load(urlMaker(product.images!![0].url))
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    setImageHeightWidth(resource)
                                    //makeToast(this@ProductDetailsActivity,"${resource.width} x ${resource.height}")
                                }

                            })

                    }

                    mutableListOf<String>().apply {
                        product.sizes?.forEach {
                            add("${it.width} x ${it.height}")
                            if (isNotEmpty()) sizeSpinner.spinner.setSelection(0)
                            if (size == product.sizes!!.size) {
                                cartVm._selectedSize.value = product.sizes!![0]
                                setupSpinner(sizeSpinner.spinner, this)
                            }
                        }
                        sizeSpinner.tvSpinnerName.text = "Size"
                    }

                    product.posterDetails?.let {
                        binding.tvTitle.text = it.title
                        if (it.short_desc.isNotEmpty() && !it.long_desc.isNullOrEmpty()) {
                            Functions.addReadMore(
                                it.long_desc,
                                binding.tvProductDescriptionLong,
                                binding.tvProductDescriptionShort
                            )
                        } else if (!it.long_desc.isNullOrEmpty()) {
                            binding.tvProductDescriptionShort.apply {
                                text = it.short_desc
                            }
                        }
                    }


                    tvBasePrice.text = "${tvBasePrice.text} ${product.basePrice}"

                    // Click Listeners ----------------------------------------------------------------------------------
                    btnChatBack.setOnClickListener {
                        onBackPressed()
                    }
                    /*productBuyingLayout.apply {
                        btnAddVariantToCart.setOnClickListener {
                            addVariantToCart(email!!,product.productId)
                        }
                    }*/
                    sizeSpinner.spinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            index: Int,
                            p3: Long
                        ) {
                            with(cartVm) {
                                _selectedVariant.value?.apply {
                                    _currentProduct.value?.let {
                                        sizeId = it.sizes?.get(index)?.sid
                                        setVariantSize(it.sizes?.get(index))
                                    }
                                }
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
//                            TODO("Not yet implemented")
                        }

                    }

                    /*
                    Todo :
                    materialSpinner.spinner.setOnItemClickListener { adapterView, view, index, l ->
                        with(cartVm) {
                            _selectedVariant.value?.apply {
                                _currentProduct.value?.let {
                                    val materialId= it.materials?.get(index)
                                    cartVm.setVariantMaterial(materialId)
                                    _selectedMaterial.value = vm._currentProductMaterial.value?.get(index)
                                }
                            }
                        }
                    }*/
                    materialSpinner.spinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            index: Int,
                            p3: Long
                        ) {
                            with(cartVm) {
                                _selectedVariant.value?.apply {
                                    _currentProduct.value?.let {
                                        val materialId = it.materials?.get(index)
                                        cartVm.setVariantMaterial(materialId)
                                        _selectedMaterial.value =
                                            vm._currentProductMaterial.value?.get(index)
                                    }
                                }
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
//                            TODO("Not yet implemented")
                        }

                    }
                    languageSpinner.spinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            index: Int,
                            p3: Long
                        ) {
                            with(cartVm) {
                                _selectedVariant.value?.apply {
                                    _currentProduct.value?.let {
                                        val languageId = it.languages?.get(index)
                                        val imgUrl = it.images?.find { it.languageId == languageId }
                                            ?.let { it1 -> urlMaker(it1.url) }

                                        Glide.with(this@ProductDetailsActivity).load(imgUrl)
                                            .into(ivProduct)
                                        cartVm.setVariantLanguage(languageId)
                                        _selectedLanguage.value =
                                            vm._currentProductLanguage.value?.get(index)
                                    }
                                }
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
//                            TODO("Not yet implemented")
                        }

                    }

                    languageSpinner.spinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            index: Int,
                            p3: Long
                        ) {
                            with(cartVm) {
                                _selectedVariant.value?.apply {
                                    _currentProduct.value?.let {
                                        val languageId = it.languages?.get(index)
                                        val imgUrl = it.images?.find { it.languageId == languageId }
                                            ?.let { it1 -> urlMaker(it1.url) }

                                        Glide.with(this@ProductDetailsActivity).asBitmap().load(imgUrl)
                                            .into(object : SimpleTarget<Bitmap>(){
                                                override fun onResourceReady(
                                                    resource: Bitmap,
                                                    transition: Transition<in Bitmap>?
                                                ) {
                                                    setImageHeightWidth(resource)
                                                }

                                            })
                                        ivProduct.apply {
                                            if (imgUrl != null) {
                                                setOnClickListener {
                                                    viewFullScreen(
                                                        this@ProductDetailsActivity,
                                                        imgUrl
                                                    )
                                                }
                                            }
                                        }
                                        cartVm.setVariantLanguage(languageId)
                                        _selectedLanguage.value =
                                            vm._currentProductLanguage.value?.get(index)
                                    }
                                }
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
//                            TODO("Not yet implemented")
                        }
                    }

                    // Text changed Listeners ----------------------------------------------------------------------------------
                    with(productBuyingLayout) {
                        evQty.setText("1")
                        evQty.doOnTextChanged { text, start, before, count ->
                            fun invalidQtyHandler() {
                                evQty.setText("1")
                                tvAmount.text =
                                    "${cartVm._selectedVariant.value?.variantPrice?.times(1)}"
                                Toast.makeText(
                                    this@ProductDetailsActivity,
                                    "Minimum Quantity is 1",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (!text.isNullOrBlank()) {
                                if (text.toString() == "0") {
                                    invalidQtyHandler()
                                } else {
                                    tvAmount.text = "${
                                        cartVm._selectedVariant.value?.variantPrice?.times(
                                            text.toString().toInt()
                                        )
                                    }"
                                }
                            }
                        }
                    }
                }
            }
        }

        val db = LocalDatabase.getDatabase(this).userDao()
        val user = db.getUser(email!!)
        user.observe(this) {
            currentUser = it
//            setupProductReviews(emptyList())
        }
        binding.rvProductReviews.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

    }

    override fun onResume() {
        super.onResume()
        vm._currentProduct.value?.let {
            email?.let { it1 -> cartVm.getMyCartVariants(it1, it.productId) }
            cartVm.refreshVariant()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun setupButtonAction(email: String, productId: Int) {
        /*
        todo : disabled until PG not get set
        if(cartVm._selectedVariant.value?.let { it1 -> isVariantInCart(it1) } == true) setupGoToCart()
        else setupAddToCart(email, productId)*/

        // GetQuote
        binding.productBuyingLayout.btnAddVariantToCart.apply {
            text = "Get Quote"
            setOnClickListener {
                cartVm._selectedVariant.value?.let {
                    val getQuoteMsg = it.let {
                        """
                        Hii , I have just visited your App and I'm interested in following product
                        Product Id :${it.productId}
                        Product Code : ${vm._currentProduct.value?.productCode ?: "None"} 
                   
                        Regards, ${currentUser?.firstName ?: "Unknown"}
                   
                         ${Constants.APP_URL}/${it.productId}/${it.sizeId}/${it.materialId}/${it.languageId}
                        """.trimIndent()

                    }
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW).apply {
//                        type = "text/plain"
//                        putExtra(Intent.EXTRA_TEXT,getQuoteMsg)
                        setData(
                            Uri.parse(
                                "https://api.whatsapp.com/send?phone=${Constants.MANN_SIGN_PHONE_NUMBER}" + "&text=" + URLEncoder.encode(
                                    getQuoteMsg,
                                    "UTF-8"
                                )
                            )
                        )
                    }, "Send to Owner"))
                }
            }
        }
        binding.productBuyingLayout.fabCallNow.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${Constants.MANN_SIGN_PHONE_NUMBER}")
            })
        }


    }

    private fun isVariantInCart(variant: Variant): Boolean {
        cartVm._currrentProductInCartVariants.value?.forEach {
            if (variant.sizeId == it.sizeId)
                if (variant.materialId == it.materialId)
                    if (variant.languageId == it.languageId)
                        return true
        }
        return false
    }

    fun addVariantToCart(email: String, productId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            cartVm.addToCart(
                email,
                cartVm._selectedVariant.value!!,
                if (binding.productBuyingLayout.evQty.text.toString() == "") 1 else
                    binding.productBuyingLayout.evQty.text.toString().toInt()
            )
            cartVm.getMyCartVariants(email, productId)
        }
    }

    private fun setupGoToCart() {
        with(binding.productBuyingLayout) {
            btnAddVariantToCart.apply {
                backgroundTintList = resources.getColorStateList(R.color.buttonColor)
                text = context.getString(R.string.go_to_cart)
                setOnClickListener {
                    startActivity(
                        Intent(
                            this@ProductDetailsActivity,
                            CartActivity::class.java
                        )
                    )
                }
            }
        }
    }

    private fun setupAddToCart(email: String, productId: Int) {
        with(binding.productBuyingLayout) {
            btnAddVariantToCart.apply {
                backgroundTintList = resources.getColorStateList(R.color.order_selected_text_color)
                text = context.getString(R.string.add_to_cart)
                setOnClickListener {
                    if (email == Constants.NO_EMAIL)
                        startActivity(Intent(this@ProductDetailsActivity, CartActivity::class.java))
                    else
                        addVariantToCart(email, productId)
                }
            }
        }
    }

    private fun calculateVariantPrice() {
        with(binding) {
            with(cartVm) {
                val areaOfSize = _selectedSize.value?.let {
                    return@let it.width * it.height
                }
                val materialPrice = _selectedMaterial.value?.price
                if (areaOfSize != null && materialPrice != null) {
                    setVariantPrice(areaOfSize * materialPrice)
                }
            }
        }
    }

    private fun setupCategoryView(name: String) {
        binding.tvCategory.text = binding.tvCategory.text.toString() + name
    }

    private fun setupSubCategoryView(name: String) {
        binding.tvSubCategory.text = binding.tvSubCategory.text.toString() + name
    }

    fun setupSpinner(spinner: Spinner, dataList: List<String>) {
        spinner.adapter = CustomSpinnerAdapter(this, dataList)
    }

    fun setupProductReviews(reviews: List<Review>) {
        currentUser?.let {
            binding.tvNoReviews.isVisible = reviews.isEmpty()
            binding.rvProductReviews.isVisible = reviews.isNotEmpty()
            binding.rvProductReviews.adapter =
                ReviewAdapter(this, reviews, reviewViewModel, currentUser)
        }
    }

    fun allowReview(email: String, productId: Int) {
        val context = this@ProductDetailsActivity
        binding.apply {
            giveReviewLayout.apply {
                setOnClickListener {
                    Toast.makeText(context, "Show view to write a review", Toast.LENGTH_SHORT)
                        .show()
                    setUpReviewBottomSheet(email, productId)
                }
                show()
            }
            ivWriteAReview.apply {
                setOnClickListener {
                    Toast.makeText(context, "Show view to write a review", Toast.LENGTH_SHORT)
                        .show()
                    setUpReviewBottomSheet(email, productId)
                }
                show()
            }
            addReviewBottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetStyle)
        }
    }

    fun setUpReviewBottomSheet(emailId: String, productId: Int) {

        val sheetView = AddReviewBottomSheetBinding
            .bind(LayoutInflater.from(this).inflate(R.layout.add_review_bottom_sheet, null))
            .apply {
                btnPublishReview.setOnClickListener {
                    Review(
                        productId = productId,
                        rating = ratingBar.rating.toInt(),
                        comment = etReview.text.toString(),
                        emailId = emailId,
                        reviewDate = LocalDateTime.now()
                    ).also {
                        vm.addReview(it)
                    }
                }
            }
        addReviewBottomSheetDialog.apply {
            setContentView(sheetView.root)
            show()
        }
    }

    fun showReviewUpdateBottomSheet(c: Context, vm: ReviewsViewModel): BottomSheetDialog {

        val bottomSheetDialog = BottomSheetDialog(c, R.style.BottomSheetStyle)
        val v = LayoutInflater.from(c).inflate(R.layout.update_review_bottom_sheet, null)
        val ratingBar = v.findViewById<RatingBar>(R.id.ratingBarUpdateReview)
        val edUpdate = v.findViewById<EditText>(R.id.edUpdateReview)
        val btnUpdateReview = v.findViewById<ImageView>(R.id.btnUpdateReviewBottomSheet)

        ratingBar.rating = vm.selectedReview.value?.rating!!.toFloat()
        edUpdate.setText(vm.selectedReview.value!!.comment.toString())
        btnUpdateReview.setOnClickListener {
            vm.updateReview(ratingBar.rating, edUpdate.text.toString())
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(v)
        return bottomSheetDialog
    }

    fun setImageHeightWidth(resource: Bitmap) {

        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)

        val wManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val density = resources.displayMetrics.density
        val displayWidth = display.widthPixels / density

        val width = resource.width.toFloat()
        val height = resource.height.toFloat()

        val GCD = gcd(width.toInt(), height.toInt())

        val AREA = 200
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

        val params = binding.ivProduct.layoutParams
        params.height = pxToDp(newHeight)
        params.width = pxToDp(newWidth)

        binding.ivProduct.layoutParams = params
        binding.ivProduct.setImageBitmap(resource)
    }

    fun gcd(a: Int, b: Int): Int {
        if (b == 0)
            return a
        return gcd(b, a % b)
    }

    private fun Context.pxToDp(value: Float): Int {
        val r: Resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value, r.displayMetrics
        ).roundToInt()
    }
}