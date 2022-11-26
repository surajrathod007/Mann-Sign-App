package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.databinding.FontItemLayoutBinding
import com.surajmanshal.mannsign.viewmodel.CustomAcpViewModel
import java.io.File

class FontAdapter(val context: Context, val fontNames: List<String>,val text : String,val vm : CustomAcpViewModel) :
    RecyclerView.Adapter<FontAdapter.FontViewHolder>() {
    inner class FontViewHolder(val binding: FontItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var txtSampleName = binding.txtSampleFontName
        var cardFont = binding.cardFont
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontViewHolder {
        return FontViewHolder(
            FontItemLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FontViewHolder, position: Int) {
        val f = fontNames[position]
        with(holder){
            var file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"/$f")
            txtSampleName.typeface = Typeface.createFromFile(file)
            txtSampleName.text = text
            holder.itemView.setOnClickListener {
                vm.selectFont(f)
                notifyItemChanged(position)
                //Toast.makeText(it.context,f + " Is selected",Toast.LENGTH_LONG).show()
            }


        }
    }

    override fun getItemCount(): Int {
        return fontNames.size
    }
}