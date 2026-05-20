package com.example.data

import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val dao: CalculationDao) {
    val allHistory: Flow<List<CalculationEntity>> = dao.getAllHistory()

    suspend fun insert(expression: String, result: String) {
        dao.insertCalculation(CalculationEntity(expression = expression, result = result))
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }
}
