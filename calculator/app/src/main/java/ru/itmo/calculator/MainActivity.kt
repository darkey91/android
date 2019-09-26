package ru.itmo.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import ru.itmo.calculator.expression.ExpressionParser
class MainActivity : AppCompatActivity() {
    private var isNewInput = true
    private var isErrorStatus = false

    private lateinit var textExpression: TextView
    private lateinit var textAnswer: TextView

    private val ERROR_MSG = "ERROR"
    private val EXPRESSION: String = "expression"
    private val ANSWER = "answer"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textExpression = findViewById(R.id.textExpresison)
        textAnswer = findViewById(R.id.textAnswer)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXPRESSION, textExpression.text.toString())
        outState.putString(ANSWER, textAnswer.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textExpression.text = savedInstanceState.getString(EXPRESSION)
        textAnswer.text = savedInstanceState.getString(ANSWER)
    }

    fun onDigit(v: View) {
        if (isNewInput) {
            onClear(v)
        }

        isNewInput = false
        val text = (v as Button).text

        if (text.equals('.') && !getLastChar().isDigit()) {
            isErrorStatus = true
            textAnswer.text = ERROR_MSG
        }
        textExpression.append(text)

    }

    fun onOperator(v: View) {
        if (isNewInput) {
            onClear(v)
            return
        }
        val operator =(v as Button).text

        if (getLastChar().toString().equals(operator)) return

        while (!getLastChar().isDigit() && !operator.equals("-")) {
            onBackspace(v)
        }

        textExpression.append(operator)
    }

    fun onClear(v: View) {
        textExpression.text = ""
        isErrorStatus = false
        isNewInput = true
    }

    fun onBackspace(v: View) {
        val size = textExpression.text.length
        if (size != 0)
            textExpression.text = textExpression.text.removeRange(size - 1, size)
        else onClear(v)
    }

    fun onEqual(v: View) {
        if (isErrorStatus) {
            textAnswer.text = ERROR_MSG
        }
        textAnswer.text = ExpressionParser().evaluate(textExpression.text.toString()).toString()
        isNewInput = true
    }

    private fun getLastChar(): Char = textExpression.text[textExpression.text.lastIndex]

}
