package com.fitaleks.chandeddit.util

import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.style.TypefaceSpan
import android.util.Log
import org.xml.sax.XMLReader


/**
 * Created by Alexander on 03.05.2018.
 */
class CodeTagHandler : Html.TagHandler {
    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader?) {
        if (tag.equals("code", true)) {
            if (opening) {
                output.setSpan(TypefaceSpan("monospace"), output.length, output.length, Spannable.SPAN_MARK_MARK)
            } else {
                Log.d("Code Tag", "Code tag encountered")
                val obj = getLast(output, TypefaceSpan::class.java)
                val where = output.getSpanStart(obj)

                output.setSpan(TypefaceSpan("monospace"), where, output.length, 0)
            }
        }
    }

    private fun getLast(text: Editable, kind: Class<*>): Any? {
        val objs = text.getSpans(0, text.length, kind)
        if (objs.isEmpty()) {
            return null
        } else {
            for (i in objs.size downTo 1) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1]
                }
            }
            return null
        }
    }
}