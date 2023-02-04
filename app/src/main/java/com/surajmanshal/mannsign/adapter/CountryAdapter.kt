package com.surajmanshal.mannsign.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.Material

class CountryAdapter(
    context: Context,
    materials : List<Material>
) : ArrayAdapter<Material>(context, 0, materials) {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View =
            convertView ?: layoutInflater.inflate(R.layout.item_double_text_spinner, parent, false)

        getItem(position)?.let { country ->
            setItemForCountry(view, country)
        }
        return view
    }


    private fun setItemForCountry(view: View, country: Material) {
            val tvCountry = view.findViewById<TextView>(R.id.tvPrimaryText)
            val tvPrice = view.findViewById<TextView>(R.id.tvSecondaryText)

            tvCountry.text = country.name
            tvPrice.text = "${country.price}/inch"
    }

}
