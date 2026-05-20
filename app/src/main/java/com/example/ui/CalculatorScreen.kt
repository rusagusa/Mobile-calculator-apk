package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CalculationEntity
import com.example.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val expression by viewModel.expression.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    var showHistory by remember { mutableStateOf(false) }
    val darkTheme = isSystemInDarkTheme()

    val backgroundColor = if (darkTheme) Color(0xFF12141C) else Color(0xFFF4F6FA)
    val cardColor = if (darkTheme) Color(0xFF1C1F2E) else Color(0xFFFFFFFF)
    val displayBgGradient = if (darkTheme) {
        Brush.verticalGradient(listOf(Color(0xFF1E2235), Color(0xFF151826)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFE8ECF5), Color(0xFFF4F6FA)))
    }

    val primaryAccent = Color(0xFF3B82F6)
    val operatorAccent = if (darkTheme) Color(0xFF6366F1) else Color(0xFF4F46E5)
    val clearAccent = if (darkTheme) Color(0xFFEF4444) else Color(0xFFDC2626)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calculator",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (darkTheme) Color.White else Color(0xFF1F2937),
                    fontFamily = FontFamily.SansSerif
                )

                IconButton(
                    onClick = { showHistory = !showHistory },
                    modifier = Modifier.testTag("history_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Show history log",
                        tint = if (darkTheme) Color(0xFF9CA3AF) else Color(0xFF4B5563)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(displayBgGradient)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val scrollState = rememberScrollState()
                    LaunchedEffect(expression) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = expression.ifEmpty { "0" },
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (darkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        IconButton(
                            onClick = { viewModel.onDeleteClick() },
                            enabled = expression.isNotEmpty(),
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("delete_backspace_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Delete last character",
                                tint = if (expression.isNotEmpty()) {
                                    clearAccent
                                } else {
                                    if (darkTheme) Color(0xFF2C3147) else Color(0xFFD1D5DB)
                                }
                            )
                        }

                        Text(
                            text = result,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (darkTheme) Color.White else Color(0xFF111827),
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2.5f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val buttonRows = listOf(
                    listOf("AC", "( )", "√", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("+/-", "0", ".", "%")
                )

                buttonRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { char ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            ) {
                                val isNumber = char.firstOrNull()?.isDigit() == true || char == "."
                                val isOperator = char in listOf("÷", "×", "-", "+", "√")
                                val isClear = char == "AC"

                                val btnBg = when {
                                    isClear -> {
                                        if (darkTheme) Color(0xFF331E20) else Color(0xFFFEE2E2)
                                    }
                                    isOperator -> {
                                        if (darkTheme) Color(0xFF231E3D) else Color(0xFFEEF2FF)
                                    }
                                    char == "%" || char == "( )" || char == "+/-" -> {
                                        if (darkTheme) Color(0xFF1C2230) else Color(0xFFEDE9FE)
                                    }
                                    else -> cardColor
                                }

                                val btnTextTint = when {
                                    isClear -> clearAccent
                                    isOperator -> operatorAccent
                                    char == "%" || char == "( )" || char == "+/-" -> {
                                        if (darkTheme) Color(0xFFA5B4FC) else Color(0xFF6366F1)
                                    }
                                    else -> if (darkTheme) Color.White else Color(0xFF374151)
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .clickable {
                                            when (char) {
                                                "AC" -> viewModel.onClearClick()
                                                "( )" -> viewModel.onBracketClick()
                                                "√" -> viewModel.onSqrtClick()
                                                "÷" -> viewModel.onOperatorClick("÷")
                                                "×" -> viewModel.onOperatorClick("×")
                                                "-" -> viewModel.onOperatorClick("-")
                                                "+" -> viewModel.onOperatorClick("+")
                                                "+/-" -> viewModel.onPlusMinusClick()
                                                "%" -> viewModel.onPercentClick()
                                                "." -> viewModel.onDecimalClick()
                                                else -> viewModel.onDigitClick(char)
                                            }
                                        }
                                        .testTag("key_$char"),
                                    colors = CardDefaults.cardColors(containerColor = btnBg),
                                    shape = CircleShape,
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = char,
                                            fontSize = if (char.length > 2) 18.sp else 24.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = btnTextTint
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.onEqualClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("key_equal"),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "=",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showHistory,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring()
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring()
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showHistory = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .fillMaxHeight(0.7f)
                        .clickable(enabled = false) {},
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Calculation History",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (darkTheme) Color.White else Color(0xFF111827)
                            )

                            Row {
                                if (history.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.clearHistory() },
                                        modifier = Modifier.testTag("clear_history_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Clear all history",
                                            tint = clearAccent
                                        )
                                    }
                                }
                                IconButton(onClick = { showHistory = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close history panel",
                                        tint = if (darkTheme) Color(0xFF9CA3AF) else Color(0xFF4B5563)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = if (darkTheme) Color(0xFF2C3147) else Color(0xFFE5E7EB)
                        )

                        if (history.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ClearAll,
                                        contentDescription = null,
                                        modifier = Modifier.size(56.dp),
                                        tint = if (darkTheme) Color(0xFF4B5563) else Color(0xFF9CA3AF)
                                    )
                                    Text(
                                        text = "Your log is empty",
                                        color = if (darkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Computations will show up here.",
                                        color = if (darkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(history, key = { it.id }) { item ->
                                    HistoryItemView(
                                        item = item,
                                        onSelect = {
                                            viewModel.onHistoryItemClick(item)
                                            showHistory = false
                                        },
                                        onDelete = {
                                            viewModel.deleteHistoryItem(item.id)
                                        },
                                        darkTheme = darkTheme,
                                        clearAccent = clearAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemView(
    item: CalculationEntity,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    darkTheme: Boolean,
    clearAccent: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (darkTheme) Color(0xFF25293C) else Color(0xFFF3F4F6)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.expression,
                    fontSize = 16.sp,
                    color = if (darkTheme) Color(0xFF9CA3AF) else Color(0xFF4B5563)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "= " + item.result,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (darkTheme) Color.White else Color(0xFF111827)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove this calculation",
                    tint = if (darkTheme) Color(0xFF4B5563) else Color(0xFF9CA3AF),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
