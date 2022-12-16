package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.ordering.CartItem
import com.surajmanshal.mannsign.databinding.CartItemLayoutBinding
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.viewmodel.CartViewModel

class CartItemAdapter(val context: Context, val list: List<CartItem>, val vm: CartViewModel) :
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    class CartItemViewHolder(val binding: CartItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgProduct = binding.imgCart
        val txtCartItemTitle = binding.txtCartItemProductName
        val txtQuantity = binding.txtCartItemQuantity
        val txtTotal = binding.txtCartItemTotal
        val btnRemoveCartItem = binding.btnRemoveCartItem
        val btnUpdateCartItem = binding.btnUpdateCart
        val edQuantity = binding.edCartQuantity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(
            CartItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val l = list[position]
        with(holder) {
            txtCartItemTitle.text = l.product?.posterDetails?.title
            txtQuantity.text = "Quantity : " + l.quantity.toString()
            txtTotal.text = "Total : " + l.totalPrice.toString()
            edQuantity.setText(l.quantity.toString())

            if(l.product?.images?.isNotEmpty() == true)
                Glide.with(context).load(Uri.parse(Functions.urlMaker(l.product?.images?.get(0)!!.url)))
                .into(imgProduct)
        }

        //remove cart
        holder.btnRemoveCartItem.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Are you sure?")
            builder.setMessage("Do you want to remove this cart?")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                vm.removeCart(l.cartItemId, l.emailId)
                //vm.getCartItems(l.emailId)
                vm._discount.postValue(0f)
            })
            builder.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                Toast.makeText(it.context, "Action cancelled", Toast.LENGTH_LONG).show()
            })
            builder.show()
        }

        holder.btnUpdateCartItem.setOnClickListener {
            if (!holder.edQuantity.text.isNullOrEmpty()) {
                vm.updateCart(l.cartItemId, holder.edQuantity.text.toString().toInt(), l.emailId)
            }else{
                Functions.makeToast(it.context,"Please enter valid quantity")
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}