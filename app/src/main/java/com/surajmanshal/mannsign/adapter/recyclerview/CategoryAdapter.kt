package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.data.model.Category
import com.surajmanshal.mannsign.data.model.SubCategory
import com.surajmanshal.mannsign.databinding.CategoryItemLayoutBinding
import com.surajmanshal.mannsign.ui.activity.ProductCategoryDetailsActivity

class CategoryAdapter(val c: Context, val list: List<SubCategory>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: CategoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtCategoryName = binding.txtCategoryName
        val cardCategory = binding.cardCategory
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            CategoryItemLayoutBinding.inflate(
                LayoutInflater.from(c),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val data = list[position]
        with(holder){
            txtCategoryName.text = data.name
            cardCategory.setOnClickListener {
                val i = Intent(c, ProductCategoryDetailsActivity::class.java)
                i.putExtra("sub",data.id)
                c.startActivity(i)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}