package ca.csf.mobile2.tp1.widget

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

//https://stackoverflow.com/questions/19217582/implicit-submit-after-hitting-done-on-the-keyboard-at-the-last-edittext/48810268#48810268
fun EditText.onSubmit(func: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            func()
        }

        true

    }
}