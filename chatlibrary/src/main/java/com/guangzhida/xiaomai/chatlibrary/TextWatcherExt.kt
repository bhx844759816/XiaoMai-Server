package com.guangzhida.xiaomai.easechatlibrary

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

/**
 * Created By bhx On 2019/8/7 0007 08:19
 */

inline fun TextView.addTextChangedListener(init: TextWatcherBridge.() -> Unit) = addTextChangedListener(TextWatcherBridge().apply(init))

class TextWatcherBridge : TextWatcher {

    private var beforeTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var afterTextChanged: ((Editable?) -> Unit)? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        beforeTextChanged?.invoke(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChanged?.invoke(s, start, before, count)
    }

    override fun afterTextChanged(s: Editable?) {
        afterTextChanged?.invoke(s)
    }

    fun beforeTextChanged(listener: (CharSequence?, Int, Int, Int) -> Unit) {
        beforeTextChanged = listener
    }

    fun onTextChanged(listener: (CharSequence?, Int, Int, Int) -> Unit) {
        onTextChanged = listener
    }

    fun afterTextChanged(listener: (Editable?) -> Unit) {
        afterTextChanged = listener
    }

}