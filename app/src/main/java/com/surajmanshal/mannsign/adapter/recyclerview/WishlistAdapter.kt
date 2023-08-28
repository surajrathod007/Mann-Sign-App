package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.databinding.ProductItemLayoutBinding
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.room.wishlist.WishListDao
import com.surajmanshal.mannsign.room.wishlist.WishListItem
import com.surajmanshal.mannsign.ui.activity.ProductDetailsActivity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class WishlistAdapter(
    val context : Context,
    val products : List<Product>,
    open val viewLifecycleOwner: LifecycleOwner,
    var wishListDao: WishListDao? = null
) : RecyclerView.Adapter<ProductViewHolder>(){

    init {
        if(wishListDao==null){
            wishListDao = LocalDatabase.getDatabase(context).wishListDao()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ProductItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        with(holder) {
                if(product.images?.isNotEmpty() == true)
                    Glide.with(context).load(Uri.parse(Functions.urlMaker(product.images?.get(0)?.url.toString()))).placeholder(
                        R.drawable.no_photo)
                        .into(imgProduct)

                product.posterDetails?.let { txtProductName.text = it.title }
//                txtProductCategory.text = product.subCategory.toString()
//                txtProductPrice.text = context.resources.getString(R.string.rupee_sign) + product.basePrice.toString()
                productCard.setOnClickListener {
                    context.startActivity(Intent(context, ProductDetailsActivity::class.java).apply {
                        putExtra(Constants.PRODUCT,product)
                    })
                }
                wishListDao?.let { wishList ->
                    btnAddToWishList.apply {
                        wishList.exist(product.productId).observe(viewLifecycleOwner){
                            if(it > 0) {
                                setFavouriteState(this, R.drawable.ic_filled_heart, R.color.error){
                                    removeFromWishList(product,this)
                                }
                            }else{
                                setFavouriteState(this,
                                    R.drawable.ic_baseline_favorite_border_24,
                                    R.color.gray_600){
                                    addToWishList(product,this)
                                }
                            }
                        }
                    }
                }/*?: Toast.makeText(context,"Not ",Toast.LENGTH_LONG).show()*/
            }

    }


    fun removeFromWishList(item: Product, view: ImageView){
        wishListDao?.let {
            CoroutineScope(Dispatchers.IO).launch{
                it.removeFromWishlist(item.productId)
            }
            setFavouriteState(view,R.drawable.ic_baseline_favorite_border_24,R.color.gray_600){
                addToWishList(item,view)
            }
        }
    }

    fun addToWishList(item: Product, view: ImageView){
        wishListDao?.let {
            CoroutineScope(Dispatchers.IO).launch {
                it.addToWishlist(WishListItem(item.productId,System.currentTimeMillis()))
            }
            setFavouriteState(view,R.drawable.ic_filled_heart,R.color.error){
                removeFromWishList(item,view)
            }
        }
    }


    fun setFavouriteState(view : ImageView, res : Int, color : Int, toggleAction : () -> Unit){
        view.setImageResource(res)
        view.imageTintList = AppCompatResources.getColorStateList(context,color)
        view.setOnClickListener {
            toggleAction()
        }
    }

}