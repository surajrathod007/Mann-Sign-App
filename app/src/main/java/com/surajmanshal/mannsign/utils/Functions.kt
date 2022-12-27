package com.surajmanshal.mannsign.utils

import android.content.Context
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.URL
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object Functions {
    fun urlMaker(imageurl :String): String {
        val fileName = imageurl.substringAfter("http://localhost:8700/images/")
        return URL.IMAGE_PATH+ fileName
    }

    fun setTypeNumber(editText: EditText){
        editText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    fun makeViewVisible(view : View){
        view.visibility = View.VISIBLE
    }
    fun makeViewGone(view : View){
        view.visibility = View.GONE
    }
    fun makeToast(context : Context, msg : String,long : Boolean=false){
        if(long){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
        }
    }

    fun addReadMore(text: String, textViewLong: TextView, textViewShort: TextView) {
        val res = textViewLong.context.resources
        val ss = SpannableString(text.substring(0, 64) + "... read more")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                addReadLess(text, textViewShort,textViewLong)
                textViewLong.visibility = View.VISIBLE
                textViewShort.visibility = View.GONE
            }

            override fun updateDrawState(txtPaint: TextPaint) {
                super.updateDrawState(txtPaint)
                txtPaint.isUnderlineText = false
                txtPaint.color = res.getColor(R.color.black)
            }
        }
        ss.setSpan(clickableSpan, ss.length - 10, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textViewShort.text = ss
        textViewShort.movementMethod = LinkMovementMethod.getInstance()
    }

    fun addReadLess(text: String, textViewShort: TextView, textViewLong: TextView) {
        val res = textViewShort.context.resources
        val ss = SpannableString("$text read less")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                addReadMore(text, textViewLong,textViewShort)
                textViewLong.visibility = View.GONE
                textViewShort.visibility = View.VISIBLE
            }

            override fun updateDrawState(textPaint: TextPaint) {
                super.updateDrawState(textPaint)
                textPaint.isUnderlineText = false
                textPaint.color = res.getColor(R.color.black)
            }
        }

        ss.setSpan(clickableSpan, ss.length - 10, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textViewLong.text = ss
        textViewLong.movementMethod = LinkMovementMethod.getInstance()
    }

    fun timeStampToDate(timestamp : String): String {
        val date = Date(timestamp.toLong())
        val t = Timestamp(timestamp.toLong())
        val d = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp.toLong()), ZoneId.systemDefault())

        return d.format(DateTimeFormatter.ofPattern("E, dd MMM yyyy hh:mm a"))
    }
}