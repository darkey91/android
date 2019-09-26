package ru.itmo.calculator.expression

class ExpressionParser {
    private var result = arrayListOf<Double>()
    private var operations = arrayListOf<Char>()

    private fun isOperator(c: Char): Boolean {
        return c == '^' || c == '%' || c == '+' || c == '-' || c == '*' || c == '/'
    }

    private fun getPriority(c: Char): Int {
        return when (c) {
            '+' -> 3
            '-' -> 3
            '*' -> 2
            '/' -> 2
            '%' -> 1
            '^' -> 0
            else -> -1
        }
    }

    private fun calculate(operation: Char): Double? {
        //ERROR
        if (result.size < 2) return null

        val right = result.last()
        result.removeAt(result.lastIndex)
        val left = result.last()
        result.removeAt(result.lastIndex)

        return when (operation) {
            '+' -> left + right
            '-' -> left - right
            '/' -> left / right
            '*' -> left * right
            '%' -> left * right / 100
            '^' -> Math.pow(left, right)
            //ERROR
            else -> null
        }
    }

    fun evaluate(expression: String): Double? {
        var isLastOperator = false
        var signMultiplier = 1

        var index = 0
        while (index < expression.length) {
            val curSymbol = expression[index]

            when {
                curSymbol.isDigit() -> {

                    val end = expression.parseNumber(index)
                    result.add(expression.substring(index, end).toDouble() * signMultiplier)
                    signMultiplier = 1
                    index = end - 1
                    isLastOperator = false

                }

                isOperator(curSymbol) -> {
                    if (curSymbol == '-' && (isLastOperator || index == 0)) {
                        signMultiplier = -1
                    } else {

                        while (operations.isNotEmpty() && getPriority(curSymbol) >= getPriority(operations.last())) {
                            val curRes = calculate(operations.last())
                            operations.removeAt(operations.lastIndex)
                            if (curRes == null) {
                                //ERROR
                                return null
                            } else {
                                result.add(curRes)
                            }
                        }
                        operations.add(curSymbol)

                    }
                    isLastOperator = true
                }
                else -> { /*ERROR*/  }
            }
            ++index
        }

        while (operations.isNotEmpty()) {
            val curRes = calculate(operations.last())
            operations.removeAt(operations.lastIndex)
            if (curRes == null) {
                //ERROR
                return null
            } else {
                result.add(curRes)
            }
        }
        return result.last()
    }

    private fun String.parseNumber(ind: Int): Int {
        var cur = ind
        while (cur < this.length && (this[cur].isDigit() || this[cur] == '.')) {
            ++cur
        }
        return cur
    }

}