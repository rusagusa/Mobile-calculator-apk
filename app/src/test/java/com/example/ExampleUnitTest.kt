package com.example

import com.example.util.ExpressionEvaluator
import org.junit.Assert.*
import org.junit.Test

/**
 * Robust unit tests verifying local calculator logic and safety.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testExpressionEvaluator_standardOperations() {
    assertEquals(10.0, ExpressionEvaluator.evaluate("3+7"), 0.0001)
    assertEquals(15.0, ExpressionEvaluator.evaluate("5×3"), 0.0001)
    assertEquals(2.0, ExpressionEvaluator.evaluate("8÷4"), 0.0001)
    assertEquals(4.5, ExpressionEvaluator.evaluate("9÷2"), 0.0001)
  }

  @Test
  fun testExpressionEvaluator_edgeAndMalformedCases() {
    // division by zero should yield NaN or infinity and not crash the JVM thread
    val divZero = ExpressionEvaluator.evaluate("5÷0")
    assertTrue(divZero.isNaN() || divZero.isInfinite())

    // empty or incomplete expressions
    assertEquals(0.0, ExpressionEvaluator.evaluate(""), 0.0)
    assertEquals(1.0, ExpressionEvaluator.evaluate("1+"), 0.0001)
    assertEquals(0.0, ExpressionEvaluator.evaluate("√"), 0.0)
    assertEquals(2.0, ExpressionEvaluator.evaluate("√4"), 0.0001)
    
    // negative sqrt should return NaN
    assertTrue(ExpressionEvaluator.evaluate("√-4").isNaN())
  }

  @Test
  fun testExpressionFormatting() {
    assertEquals("100", ExpressionEvaluator.formatResult(100.0))
    assertEquals("5.25", ExpressionEvaluator.formatResult(5.25))
    assertEquals("Error", ExpressionEvaluator.formatResult(Double.NaN))
    assertEquals("Infinity", ExpressionEvaluator.formatResult(Double.POSITIVE_INFINITY))
  }
}
