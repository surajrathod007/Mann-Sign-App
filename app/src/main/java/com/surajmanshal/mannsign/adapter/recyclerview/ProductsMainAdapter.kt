package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.data.model.product.MainPoster
import com.surajmanshal.mannsign.databinding.ItemProductsMainLayoutBinding
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.viewmodel.HomeViewModel

class ProductsMainAdapter(
    val c: Context,
    val l: List<MainPoster>,
    val vm: HomeViewModel,
    val viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<ProductsMainAdapter.ProductMainViewHolder>() {

    class ProductMainViewHolder(binding: ItemProductsMainLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtCat = binding.txtProductMainCategory
        val rvChild = binding.rvChildProducts
        val btnMore = binding.btnViewMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductMainViewHolder {
        return ProductMainViewHolder(
            ItemProductsMainLayoutBinding.inflate(
                LayoutInflater.from(c),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ProductMainViewHolder, position: Int) {
        val d = l[position]

            vm.getSubCategoryById(d.subCategory.toInt()){
                holder.txtCat.text = it
            }

        with(holder){
            rvChild.adapter = ProductAdapter(c,d.posters,vm)
            btnMore.setOnClickListener {
                Functions.makeToast(c,d.subCategory)
            }
        }
    }

    override fun getItemCount(): Int {
        return l.size
    }
}