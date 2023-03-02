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
import com.surajmanshal.mannsign.room.wishlist.WishListDao
import com.surajmanshal.mannsign.room.wishlist.WishListItem
import com.surajmanshal.mannsign.ui.activity.ProductDetailsActivity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductAdapter(
    val activity: Context,
    val list: List<Product>,
    val vm: HomeViewModel = HomeViewModel(),
    val viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    lateinit var wishList : WishListDao
    class ProductViewHolder(val binding: ProductItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgProduct = binding.imgProductImage
        val txtProductName = binding.txtProductName
        val txtProductPrice = binding.txtProductPrice
        val txtProductCategory = binding.txtProductCategory
        val btnAddToWishList = binding.btnAddToWishlist
        val productCard = binding.productCard
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        vm.setUpWishlistSource(activity).also {
            wishList = it
        }
        return ProductViewHolder(
            ProductItemLayoutBinding.inflate(
                LayoutInflater.from(activity),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data = list[position]
        val context = holder.itemView.context
        //todo : fetch banners
        if (data.posterDetails != null) {
            with(holder) {
                if(data.images?.isNotEmpty() == true)
                Glide.with(context).load(Uri.parse(Functions.urlMaker(data.images?.get(0)?.url.toString()))).placeholder(
                    R.drawable.no_photo)
                    .into(imgProduct)

                txtProductName.text = data.posterDetails!!.title
                txtProductCategory.text = data.subCategory.toString()
                txtProductPrice.text = context.resources.getString(R.string.rupee_sign) + data.basePrice.toString()
                productCard.setOnClickListener {
                    context.startActivity(Intent(context,ProductDetailsActivity::class.java).apply {
                        putExtra(Constants.PRODUCT,data)
                    })
                }
                wishList?.let { wishList ->
                    btnAddToWishList.apply {
                        wishList.exist(data.productId).observe(viewLifecycleOwner){
                            if(it > 0) {
                                setFavouriteState(this,R.drawable.ic_filled_heart,R.color.error){
                                    removeFromWishList(data,this)
                                }
                            }else{
                                setFavouriteState(this,R.drawable.ic_baseline_favorite_border_24,R.color.gray_600){
                                    addToWishList(data,this)
                                }
                            }
                        }
                    }
                }/*?: Toast.makeText(context,"Not ",Toast.LENGTH_LONG).show()*/
            }
        }
    }
    fun removeFromWishList(item: Product, view: ImageView){
        wishList?.let {
            CoroutineScope(Dispatchers.IO).launch{
                it.removeFromWishlist(item.productId)
            }
            setFavouriteState(view,R.drawable.ic_baseline_favorite_border_24,R.color.gray_600){
                addToWishList(item,view)
            }
        }
    }

    fun addToWishList(item: Product, view: ImageView){
        wishList?.let {
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
        view.imageTintList = AppCompatResources.getColorStateList(activity,color)
        view.setOnClickListener {
            toggleAction()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}