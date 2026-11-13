package com.example.calculator

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var display: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF000000.toInt())
            setPadding(24, 48, 24, 24)
        }

        display = EditText(this).apply {
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFF000000.toInt())
            textSize = 40f
            gravity = Gravity.END
            inputType = InputType.TYPE_NULL
            isCursorVisible = false
            keyListener = null
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(display)

        val grid = GridLayout(this).apply {
            columnCount = 4
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0, 1f
            )
        }
        root.addView(grid)

        val keys = listOf("C", "⌫", "%", "÷", "7", "8", "9", "×",
                          "4", "5", "6", "−", "1", "2", "3", "+",
                          "0", "", ".", "=")
        for (k in keys) {
            val btn = Button(this).apply {
                text = k
                textSize = 24f
                setTextColor(0xFFFFFFFF.toInt())
                when (k) {
                    "C", "⌫", "%" -> setBackgroundColor(0xFFAAAAAA.toInt())
                    "÷", "×", "−", "+", "=" -> setBackgroundColor(0xFFFF9500.toInt())
                    else -> setBackgroundColor(0xFF333333.toInt())
                }
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnWeight = 1f
                    rowWeight = 1f
                    setMargins(6, 6, 6, 6)
                }
                setOnClickListener { onKey(k) }
            }
            if (k.isNotEmpty()) grid.addView(btn) else grid.addView(View(this), GridLayout.LayoutParams().apply { width = 0; height = 0; columnWeight = 1f; rowWeight = 1f })
        }

        setContentView(root)
        if (savedInstanceState != null) display.setText(savedInstanceState.getString("expr", ""))
    }

    private var resultShown = false

    private fun onKey(k: String) {
        when (k) {
            "C" -> { display.setText(""); resultShown = false }
            "⌫" -> { if (!resultShown) display.setText(display.text.dropLast(1)) }
            "=" -> {
                try {
                    val v = ExpressionParser.evaluate(display.text.toString())
                    display.setText(DecimalFormat("#.##########").format(v))
                    resultShown = true
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid expression", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                if (resultShown) { display.setText(""); resultShown = false }
                display.append(k)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("expr", display.text.toString())
    }
}

object ExpressionParser {
    fun evaluate(input: String): Double {
        val tokens = tokenize(input)
        var pos = 0

        fun parsePrimary(): Double {
            var sign = 1.0
            if (pos < tokens.size && tokens[pos] == "-") { sign = -1.0; pos++ }
            val num = tokens.getOrNull(pos)?.toDoubleOrNull() ?: throw IllegalArgumentException()
            pos++
            var value = num
            if (pos < tokens.size && tokens[pos] == "%") { value /= 100.0; pos++ }
            return sign * value
        }

        fun parseTerm(): Double {
            var result = parsePrimary()
            while (pos < tokens.size && (tokens[pos] == "×" || tokens[pos] == "÷")) {
                val op = tokens[pos]; pos++
                val rhs = parsePrimary()
                result = if (op == "×") result * rhs else {
                    if (rhs == 0.0) throw ArithmeticException()
                    result / rhs
                }
            }
            return result
        }

        fun parseExpr(): Double {
            var result = parseTerm()
            while (pos < tokens.size && (tokens[pos] == "+" || tokens[pos] == "-")) {
                val op = tokens[pos]; pos++
                val rhs = parseTerm()
                result = if (op == "+") result + rhs else result - rhs
            }
            return result
        }

        val value = parseExpr()
        if (pos != tokens.size) throw IllegalArgumentException()
        return value
    }

    private fun tokenize(input: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < input.length) {
            val c = input[i]
            when {
                c.isDigit() || c == '.' -> {
                    val start = i
                    while (i < input.length && (input[i].isDigit() || input[i] == '.')) i++
                    tokens.add(input.substring(start, i))
                }
                c in "+-×÷%" -> { tokens.add(c.toString()); i++ }
                else -> throw IllegalArgumentException()
            }
        }
        return tokens
    }
}
