package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.data.model.Category
import com.surajmanshal.mannsign.data.model.SubCategory
import com.surajmanshal.mannsign.databinding.CategoryItemLayoutBinding

class CategoryAdapter(val c: Context, val list: List<SubCategory>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: CategoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtCategoryName = binding.txtCategoryName
        val imgCategory = binding.imgCategory
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
            //TODO : Loading Image....
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}