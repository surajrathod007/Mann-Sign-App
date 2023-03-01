package com.surajmanshal.mannsign.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.databinding.ItemDoubleTextSpinnerBinding

class CustomSpinnerAdapter(
    context: Context,
    materials : List<String>
) : ArrayAdapter<String>(context, 0, materials) {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = layoutInflater.inflate(R.layout.item_double_text_spinner, null, false)
        return setItemForCountry(view,position,true)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return setItemForCountry(
            convertView ?:
            layoutInflater.inflate(
                R.layout.item_double_text_spinner, parent, false
            ), position
        )
    }



    private fun setItemForCountry(view: View, position: Int,selected : Boolean=false): View {

        val item = getItem(position)
            ?:
        return view

        ItemDoubleTextSpinnerBinding.bind(view).apply {
            tvPrimaryText.text = item
            ivDropDown.isVisible = selected
            tvSecondaryText.isVisible = selected
            return root
        }
    }

}
