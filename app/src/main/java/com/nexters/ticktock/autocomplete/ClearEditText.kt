package com.nexters.ticktock.autocomplete

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.content.ContextCompat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.Toast
import com.nexters.ticktock.R


class ClearEditText : AppCompatEditText, TextWatcher, View.OnTouchListener, View.OnFocusChangeListener {

    private var clearDrawable: Drawable? = null
    private var onFocusChange: View.OnFocusChangeListener? = null
    private var onTouchListener: View.OnTouchListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun setOnFocusChangeListener(onFocusChangeListener: View.OnFocusChangeListener) {
        this.onFocusChange = onFocusChangeListener
    }

    override fun setOnTouchListener(onTouchListener: View.OnTouchListener) {
        this.onTouchListener = onTouchListener
    }

    private fun init() {

        val tempDrawable = ContextCompat.getDrawable(context, R.drawable.abc_ic_clear_material)
        clearDrawable = DrawableCompat.wrap(tempDrawable!!)
        DrawableCompat.setTintList(clearDrawable!!, hintTextColors)
        clearDrawable!!.setBounds(0, 0, clearDrawable!!.intrinsicWidth, clearDrawable!!.intrinsicHeight)

        setClearIconVisible(false)

        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        addTextChangedListener(this)
    }


    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            setClearIconVisible(text!!.length > 0)
        } else {
            setClearIconVisible(false)
        }

        if (onFocusChange != null) {
            onFocusChange!!.onFocusChange(view, hasFocus)
        }
    }


    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val x = motionEvent.x.toInt()
        if (clearDrawable!!.isVisible && x > width - paddingRight - clearDrawable!!.intrinsicWidth) {
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                error = null
                setText(null)
            }
            return true
        }

        return if (onTouchListener != null) {
            onTouchListener!!.onTouch(view, motionEvent)
        } else {
            false
        }

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isFocused) {
            setClearIconVisible(s.length > 0)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {}


    private fun setClearIconVisible(visible: Boolean) {
        clearDrawable!!.setVisible(visible, false)
        setCompoundDrawables(null, null, if (visible) clearDrawable else null, null)
    }
}