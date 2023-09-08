package com.appttude.h_mal.farmr.base

import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.core.view.children


open class FormFragment<V : BaseViewModel>(@LayoutRes contentLayoutId: Int) : BaseFragment<V>(contentLayoutId) {
    private val initialFormData = mutableMapOf<Int, String>()
    private val formData = mutableMapOf<Int, String>()

    fun applyFormListener(view: ViewGroup) {
        view.children.forEach {
            if (it is EditText) {
                initialFormData[it.id] = it.text.trim().toString()
                setDataInMap(it.id, it.text.trim().toString())
                it.addCustomTextWatch()
            } else if (it is ViewGroup) {
                applyFormListener(it)
            }
        }
    }

    fun didFormChange(): Boolean {
       return !(initialFormData.all { (k, v) ->
           formData[k] == v
       })
    }

    private fun EditText.addCustomTextWatch() {
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setDataInMap(id, p0.toString())
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    private fun setDataInMap(id: Int, text: String) {
        formData[id] = text
    }
}