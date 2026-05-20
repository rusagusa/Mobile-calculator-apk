package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CalculationEntity
import com.example.data.CalculationRepository
import com.example.util.ExpressionEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CalculatorViewModel(private val repository: CalculationRepository) : ViewModel() {

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _history = MutableStateFlow<List<CalculationEntity>>(emptyList())
    val history: StateFlow<List<CalculationEntity>> = _history.asStateFlow()

    private var isEvaluated = false

    init {
        viewModelScope.launch {
            repository.allHistory.collectLatest { list ->
                _history.value = list
            }
        }
    }

    fun onDigitClick(digit: String) {
        if (isEvaluated) {
            _expression.value = digit
            isEvaluated = false
        } else {
            _expression.value += digit
        }
        updateRealtimePreview()
    }

    fun onOperatorClick(op: String) {
        isEvaluated = false
        val current = _expression.value
        if (current.isEmpty()) {
            if (op == "-") {
                _expression.value = "-"
            }
            return
        }
        
        val lastChar = current.last()
        if (lastChar == '+' || lastChar == '-' || lastChar == '×' || lastChar == '÷') {
            _expression.value = current.dropLast(1) + op
        } else {
            _expression.value += op
        }
    }

    fun onClearClick() {
        _expression.value = ""
        _result.value = ""
        isEvaluated = false
    }

    fun onDeleteClick() {
        isEvaluated = false
        val current = _expression.value
        if (current.isNotEmpty()) {
            _expression.value = current.dropLast(1)
            updateRealtimePreview()
        }
    }

    fun onPercentClick() {
        isEvaluated = false
        val current = _expression.value
        if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
            _expression.value += "%"
            updateRealtimePreview()
        }
    }

    fun onSqrtClick() {
        if (isEvaluated) {
            _expression.value = "√" + _result.value
            isEvaluated = false
        } else {
            val current = _expression.value
            if (current.isEmpty() || current.last() == '+' || current.last() == '-' || current.last() == '×' || current.last() == '÷' || current.last() == '(') {
                _expression.value += "√"
            } else {
                _expression.value += "×√"
            }
        }
        updateRealtimePreview()
    }

    fun onPlusMinusClick() {
        _expression.value = toggleLastNumberSign(_expression.value)
        updateRealtimePreview()
    }

    fun onDecimalClick() {
        if (isEvaluated) {
            _expression.value = "0."
            isEvaluated = false
            return
        }
        val current = _expression.value
        if (current.isEmpty()) {
            _expression.value = "0."
            return
        }
        
        val parts = current.split(Regex("[+×÷\\-√%]"))
        val lastPart = parts.last()
        if (!lastPart.contains(".")) {
            _expression.value += "."
        }
    }

    fun onBracketClick() {
        if (isEvaluated) {
            _expression.value = "("
            isEvaluated = false
            return
        }
        val current = _expression.value
        val openCount = current.count { it == '(' }
        val closeCount = current.count { it == ')' }
        
        if (openCount > closeCount && current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
            _expression.value += ")"
        } else {
            _expression.value += "("
        }
        updateRealtimePreview()
    }

    fun onEqualClick() {
        val expr = _expression.value
        if (expr.isEmpty()) return
        
        val value = ExpressionEvaluator.evaluate(expr)
        if (value.isNaN() || value.isInfinite()) {
            _result.value = "Error"
            return
        }
        
        val formattedResult = ExpressionEvaluator.formatResult(value)
        _result.value = formattedResult
        isEvaluated = true
        
        viewModelScope.launch {
            repository.insert(expr, formattedResult)
        }
    }

    fun onHistoryItemClick(item: CalculationEntity) {
        _expression.value = item.expression
        _result.value = item.result
        isEvaluated = true
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    private fun updateRealtimePreview() {
        val expr = _expression.value
        if (expr.isEmpty()) {
            _result.value = ""
            return
        }
        if (expr == "-" || expr == "√") {
            _result.value = ""
            return
        }
        val value = ExpressionEvaluator.evaluate(expr)
        if (!value.isNaN() && !value.isInfinite()) {
            _result.value = ExpressionEvaluator.formatResult(value)
        }
    }

    private fun toggleLastNumberSign(expr: String): String {
        if (expr.isEmpty()) return "-"
        
        var i = expr.length - 1
        while (i >= 0 && (expr[i].isDigit() || expr[i] == '.')) {
            i--
        }
        
        val numberIndex = i + 1
        if (numberIndex == 0) {
            return if (expr.startsWith("-")) {
                expr.substring(1)
            } else {
                "-$expr"
            }
        }
        
        val precedingCharIndex = numberIndex - 1
        val precedingChar = expr[precedingCharIndex]
        
        if (precedingChar == '-') {
            val isUnaryMinus = precedingCharIndex == 0 || expr[precedingCharIndex - 1] in listOf('+', '-', '×', '÷', '(')
            if (isUnaryMinus) {
                return expr.substring(0, precedingCharIndex) + expr.substring(numberIndex)
            }
        }
        
        return expr.substring(0, numberIndex) + "-" + expr.substring(numberIndex)
    }

    class Factory(private val repository: CalculationRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CalculatorViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
