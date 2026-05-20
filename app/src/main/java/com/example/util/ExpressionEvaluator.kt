package com.example.util

import kotlin.math.sqrt

object ExpressionEvaluator {

    fun evaluate(expression: String): Double {
        if (expression.isEmpty()) return 0.0
        val clean = expression
            .replace("×", "*")
            .replace("÷", "/")
        
        return parseAndEval(clean)
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return "Infinity"
        
        val longVal = value.toLong()
        if (value == longVal.toDouble()) {
            return longVal.toString()
        }
        
        val str = value.toString()
        if (str.length > 12) {
            val df = java.text.DecimalFormat("#.########")
            return df.format(value)
        }
        return str
    }

    private fun parseAndEval(expr: String): Double {
        try {
            val tokens = tokenize(expr)
            if (tokens.isEmpty()) return 0.0
            
            val resolvedTokens = resolveUnaryAndPrecedence(tokens)
            val rpn = shuntingYard(resolvedTokens)
            return evaluateRPN(rpn)
        } catch (e: Exception) {
            return Double.NaN
        }
    }

    private fun tokenize(expr: String): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        val len = expr.length
        
        while (i < len) {
            val c = expr[i]
            when {
                c.isWhitespace() -> {
                    i++
                }
                c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == '%' || c == '√' -> {
                    result.add(c.toString())
                    i++
                }
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    result.add(sb.toString())
                }
                else -> {
                    i++
                }
            }
        }
        return result
    }

    private fun resolveUnaryAndPrecedence(tokens: List<String>): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val current = tokens[i]
            when {
                current == "√" -> {
                    if (i + 1 < tokens.size) {
                        val next = tokens[i + 1]
                        val nextVal = next.toDoubleOrNull()
                        if (nextVal != null) {
                            if (nextVal < 0.0) {
                                result.add(Double.NaN.toString())
                            } else {
                                val valSqrt = sqrt(nextVal)
                                result.add(valSqrt.toString())
                            }
                            i += 2
                            continue
                        }
                    }
                    result.add(current)
                    i++
                }
                current == "%" -> {
                    if (result.isNotEmpty()) {
                        val lastIndex = result.lastIndex
                        val prev = result[lastIndex]
                        val prevVal = prev.toDoubleOrNull()
                        if (prevVal != null) {
                            result[lastIndex] = (prevVal / 100.0).toString()
                        }
                    }
                    i++
                }
                current == "-" -> {
                    val isUnary = i == 0 || tokens[i - 1] in listOf("+", "-", "*", "/", "(", "√")
                    if (isUnary && i + 1 < tokens.size && tokens[i+1].toDoubleOrNull() != null) {
                        result.add("-" + tokens[i+1])
                        i += 2
                        continue
                    }
                    result.add(current)
                    i++
                }
                else -> {
                    result.add(current)
                    i++
                }
            }
        }
        return result
    }

    private fun shuntingYard(tokens: List<String>): List<String> {
        val rpn = mutableListOf<String>()
        val stack = mutableListOf<String>()
        
        for (token in tokens) {
            if (token.toDoubleOrNull() != null) {
                rpn.add(token)
            } else if (token == "(") {
                stack.add(token)
            } else if (token == ")") {
                while (stack.isNotEmpty() && stack.last() != "(") {
                    rpn.add(stack.removeAt(stack.lastIndex))
                }
                if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex)
            } else {
                while (stack.isNotEmpty() && precedence(stack.last()) >= precedence(token)) {
                    rpn.add(stack.removeAt(stack.lastIndex))
                }
                stack.add(token)
            }
        }
        while (stack.isNotEmpty()) {
            rpn.add(stack.removeAt(stack.lastIndex))
        }
        return rpn
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> 0
        }
    }

    private fun evaluateRPN(rpn: List<String>): Double {
        val valStack = mutableListOf<Double>()
        for (token in rpn) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                valStack.add(num)
            } else {
                if (valStack.size < 2) continue
                val b = valStack.removeAt(valStack.lastIndex)
                val a = valStack.removeAt(valStack.lastIndex)
                val res = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> {
                        if (b == 0.0) Double.NaN else a / b
                    }
                    else -> 0.0
                }
                valStack.add(res)
            }
        }
        return if (valStack.isNotEmpty()) valStack.last() else 0.0
    }
}
