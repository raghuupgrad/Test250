package com.example.calculator

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var current = ""
    private var first = 0.0
    private var op = ""
    private lateinit var display: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(0xFF000000.toInt())
        root.setPadding(24, 48, 24, 24)

        display = EditText(this)
        display.setTextColor(0xFFFFFFFF.toInt())
        display.setBackgroundColor(0xFF000000.toInt())
        display.textSize = 40f
        display.gravity = Gravity.END
        display.inputType = InputType.TYPE_NULL
        display.keyListener = null
        display.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        root.addView(display)

        val keys = arrayOf(
            arrayOf("C", "⌫", "÷", "×"),
            arrayOf("7", "8", "9", "-"),
            arrayOf("4", "5", "6", "+"),
            arrayOf("1", "2", "3", "="),
            arrayOf("0", ".", "", "")
        )
        for (row in keys) {
            val line = LinearLayout(this)
            line.orientation = LinearLayout.HORIZONTAL
            line.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
            for (k in row) {
                if (k.isEmpty()) continue
                val b = Button(this)
                b.text = k
                b.textSize = 24f
                b.setTextColor(0xFFFFFFFF.toInt())
                b.setBackgroundColor(
                    if (k == "C" || k == "⌫") 0xFFAAAAAA.toInt()
                    else if (k in arrayOf("÷", "×", "-", "+", "=")) 0xFFFF9500.toInt()
                    else 0xFF333333.toInt()
                )
                b.layoutParams = LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1f
                ).apply { setMargins(6, 6, 6, 6) }
                b.setOnClickListener { press(k) }
                line.addView(b)
            }
            root.addView(line)
        }
        setContentView(root)
    }

    private fun press(k: String) {
        when {
            k in "0123456789." -> {
                current += k
                show(current)
            }
            k == "C" -> {
                current = ""
                first = 0.0
                op = ""
                show("")
            }
            k == "⌫" -> {
                current = current.dropLast(1)
                show(current)
            }
            k in arrayOf("+", "-", "×", "÷") -> {
                if (current.isNotEmpty()) {
                    first = current.toDouble()
                    current = ""
                }
                op = k
                show("")
            }
            k == "=" -> {
                if (current.isEmpty() || op.isEmpty()) return
                val second = current.toDouble()
                val r = when (op) {
                    "+" -> first + second
                    "-" -> first - second
                    "×" -> first * second
                    else -> if (second == 0.0) {
                        Toast.makeText(this, "Cannot divide by 0", Toast.LENGTH_SHORT).show()
                        return
                    } else first / second
                }
                show(if (r == r.toLong().toDouble()) r.toLong().toString() else r.toString())
                current = ""
                op = ""
            }
        }
    }

    private fun show(s: String) {
        display.setText(s)
    }
}
