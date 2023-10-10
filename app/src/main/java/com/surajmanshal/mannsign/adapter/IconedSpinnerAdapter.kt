package com.surajmanshal.mannsign.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.payment.UPIApp
import com.surajmanshal.mannsign.databinding.ItemIconedTextSpinnerBinding

class IconedSpinnerAdapter(
    context: Context,
    materials : List<UPIApp>
) : ArrayAdapter<UPIApp>(context, 0, materials) {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = layoutInflater.inflate(R.layout.item_iconed_text_spinner, null, false)
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

        val upiApp = getItem(position)
            ?:
        return view

        ItemIconedTextSpinnerBinding.bind(view).apply {
            tvSecondaryText.text = upiApp.name
//            ivDropDown.isVisible = selected   // previously used to manually give it a look of a spinner
            /*if(selected){
                tvSecondaryText.hide()
            }else{
                tvSecondaryText.text = "₹ ${material.price}/Inch \u00B2"
            }*/
            upiApp.icon?.let { ivAppIcon.setImageResource(it) }
            return root
        }
    }

}
