package com.example.forit

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ChouhuResource
import com.example.model.OtherExpenseResource
import com.example.model.PeopleExpenseResource
import com.example.model.SaleResource
import kotlin.random.Random

@Composable
fun SalesScreen(
    selectedDate: String,
    sales: List<SaleResource>,
    hoards: List<SaleResource>,
    chouhus: List<ChouhuResource>,
    peopleExpenses: List<PeopleExpenseResource>,
    otherExpenses: List<OtherExpenseResource>,
    onSaveSale: (List<SaleResource>) -> Unit,
    onSaveHoard: (List<SaleResource>) -> Unit,
    onDeleteHoard: (List<String>) -> Unit,
) {
    // We expect exactly 4 fixed items for levels 1, 2, 3, 4(杂货) in sales list
    val fixedLevels = listOf("1", "2", "3", "4")
    val saleItems = remember { mutableStateListOf<SaleUiState>() }
    var editingSaleIndex by remember { mutableIntStateOf(-1) }

    val hoardKeys = remember { mutableSetOf<String>() }
    val hoardItems = remember { mutableStateListOf<SaleUiState>() }
    var editingHoardIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(sales, selectedDate) {
        saleItems.clear()
        
        val currentKeys = mutableSetOf<String>()
        // Initialize existing keys to prevent any collisions
        sales.forEach { currentKeys.add(it.key) }
        
        fixedLevels.forEach { level ->
            val existing = sales.find { it.level == level }
            if (existing != null) {
                saleItems.add(existing.asUiState())
            } else {
                val newKey = generateUniqueKey(currentKeys, selectedDate)
                saleItems.add(
                    SaleUiState(
                        key = newKey,
                        level = level,
                        unitPrice = "",
                        count = ""
                    )
                )
            }
        }
        if (editingSaleIndex >= saleItems.size) editingSaleIndex = -1
    }

    LaunchedEffect(hoards, selectedDate) {
        hoardItems.clear()
        
        val currentKeys = mutableSetOf<String>()
        // Initialize existing keys to prevent any collisions
        hoards.forEach { currentKeys.add(it.key) }
        
        fixedLevels.forEach { level ->
            val existing = hoards.find { it.level == level }
            if (existing != null) {
                hoardItems.add(existing.asUiState())
            } else {
                val newKey = generateUniqueKey(currentKeys, selectedDate)
                hoardItems.add(
                    SaleUiState(
                        key = newKey,
                        level = level,
                        unitPrice = "",
                        count = ""
                    )
                )
            }
        }
        if (editingHoardIndex >= hoardItems.size) editingHoardIndex = -1
    }

    val totalIncome = saleItems.sumOf { (it.unitPrice.toDoubleOrNull() ?: 0.0) * (it.count.toDoubleOrNull() ?: 0.0) }
    val totalIncomeText = if (totalIncome == 0.0) "0" else if (totalIncome % 1.0 == 0.0) totalIncome.toInt().toString() else String.format(Locale.getDefault(), "%.1f", totalIncome)

    val totalHoard = hoardItems.sumOf { it.count.toDoubleOrNull()?.toInt() ?: 0 }

    val totalChouhu = chouhus.sumOf { it.unitPrice * it.count }
    val totalPeopleExpense = peopleExpenses.sumOf { it.price }
    val totalOtherExpense = otherExpenses.sumOf { it.unitPrice * it.count }
    val totalExpense = totalPeopleExpense + totalOtherExpense

    val profit = totalIncome - totalChouhu - totalExpense
    val profitText = if (profit == 0.0) "0" else if (profit % 1.0 == 0.0) profit.toInt().toString() else String.format(Locale.getDefault(), "%.1f", profit)
    val totalChouhuText = if (totalChouhu == 0.0) "0" else if (totalChouhu % 1.0 == 0.0) totalChouhu.toInt().toString() else String.format(Locale.getDefault(), "%.1f", totalChouhu)
    val totalExpenseText = if (totalExpense == 0.0) "0" else if (totalExpense % 1.0 == 0.0) totalExpense.toInt().toString() else String.format(Locale.getDefault(), "%.1f", totalExpense)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.background(Color(0xFFE3F2FD), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("售卖记录", color = Color(0xFF1976D2), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("售卖总价: ¥$totalIncomeText", color = Color(0xFF1976D2), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                
                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("级别", modifier = Modifier.weight(1.2f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("斤数", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("单价", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("总价", modifier = Modifier.weight(1.2f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.End)
                }
            }
        }

        items(saleItems.size, key = { "sale_${saleItems[it].key}" }) { index ->
            val item = saleItems[index]
            CompactSaleItemRow(
                expense = item,
                isEditing = editingSaleIndex == index,
                onClick = {
                    editingSaleIndex = if (editingSaleIndex == index) -1 else index
                    editingHoardIndex = -1
                },
                onValueChange = { updatedItem ->
                    saleItems[index] = updatedItem
                    onSaveSale(saleItems.map { it.asModel(selectedDate) })
                }
            )
        }

        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.background(Color(0xFFFFEBEE), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("今日囤积", color = Color(0xFFD32F2F), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("囤积总数量: $totalHoard 斤", color = Color(0xFFD32F2F), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 0 until (hoardItems.size + 1) / 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0..1) {
                            val index = row * 2 + col
                            if (index < hoardItems.size) {
                                val item = hoardItems[index]
                                Box(modifier = Modifier.weight(1f)) {
                                    CompactHoardItem(
                                        expense = item,
                                        isEditing = editingHoardIndex == index,
                                        onClick = {
                                            editingHoardIndex = if (editingHoardIndex == index) -1 else index
                                            editingSaleIndex = -1
                                        },
                                        onValueChange = { updatedItem ->
                                            hoardItems[index] = updatedItem
                                            onSaveHoard(hoardItems.map { it.asModel(selectedDate) })
                                        }
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("今日盈利", fontSize = 16.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("售卖(¥$totalIncomeText) - 抽户(¥$totalChouhuText) - 开支(¥$totalExpenseText)", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                    Text(
                        text = "¥$profitText",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
        }
    }
}



@Composable
fun CompactHoardItem(
    expense: SaleUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (SaleUiState) -> Unit,
) {
    val title = when (expense.level) {
        "1" -> "1级货"
        "2" -> "2级货"
        "3" -> "3级货"
        "4" -> "杂货"
        "囤积" -> "囤积"
        else -> "${expense.level}级货"
    }

    val tagBg = when (expense.level) {
        "1" -> Color(0xFFE8F5E9)
        "2" -> Color(0xFFE3F2FD)
        "3" -> Color(0xFFFFF3E0)
        "4" -> Color(0xFFF5F5F5)
        "囤积" -> Color(0xFFFFF8E1)
        else -> Color(0xFFE8F5E9)
    }

    val tagText = when (expense.level) {
        "1" -> PrimaryGreen
        "2" -> Color(0xFF1976D2)
        "3" -> Color(0xFFF57C00)
        "4" -> Color(0xFF757575)
        "囤积" -> Color(0xFFFBC02D)
        else -> PrimaryGreen
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Level
        Box(
            modifier = Modifier
                .weight(1f)
                .background(tagBg, RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(title, color = tagText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.width(8.dp))

        // Count
        if (isEditing) {
            BasicTextField(
                value = expense.count,
                onValueChange = { onValueChange(expense.copy(count = it)) },
                modifier = Modifier
                    .weight(1.5f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    if (expense.count.isEmpty()) {
                        Text("斤数", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (expense.count.isEmpty()) "0 斤" else "${expense.count} 斤",
                modifier = Modifier.weight(1.5f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (expense.count.isEmpty()) TextGray else Color.Black
            )
        }
    }
}

@Composable
fun CompactSaleItemRow(
    expense: SaleUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (SaleUiState) -> Unit,
) {
    val title = when (expense.level) {
        "1" -> "1级货"
        "2" -> "2级货"
        "3" -> "3级货"
        "4" -> "杂货"
        "囤积" -> "囤积"
        else -> "${expense.level}级货"
    }

    val tagBg = when (expense.level) {
        "1" -> Color(0xFFE8F5E9)
        "2" -> Color(0xFFE3F2FD)
        "3" -> Color(0xFFFFF3E0)
        "4" -> Color(0xFFF5F5F5)
        "囤积" -> Color(0xFFFFF8E1)
        else -> Color(0xFFE8F5E9)
    }

    val tagText = when (expense.level) {
        "1" -> PrimaryGreen
        "2" -> Color(0xFF1976D2)
        "3" -> Color(0xFFF57C00)
        "4" -> Color(0xFF757575)
        "囤积" -> Color(0xFFFBC02D)
        else -> PrimaryGreen
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Level
        Box(
            modifier = Modifier
                .weight(1f)
                .background(tagBg, RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(title, color = tagText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.width(8.dp))

        // Count
        if (isEditing) {
            BasicTextField(
                value = expense.count,
                onValueChange = { onValueChange(expense.copy(count = it)) },
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    if (expense.count.isEmpty()) {
                        Text("斤数", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (expense.count.isEmpty()) "0 斤" else "${expense.count} 斤",
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (expense.count.isEmpty()) TextGray else Color.Black
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

    // Unit Price
        if (isEditing) {
            BasicTextField(
                value = expense.unitPrice,
                onValueChange = { onValueChange(expense.copy(unitPrice = it)) },
                modifier = Modifier
                    .weight(1.5f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    if (expense.unitPrice.isEmpty()) {
                        Text("单价", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (expense.unitPrice.isEmpty()) "¥0" else "¥${expense.unitPrice}",
                modifier = Modifier.weight(1.5f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (expense.unitPrice.isEmpty()) TextGray else Color.Black
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Total
        val total = (expense.unitPrice.toDoubleOrNull() ?: 0.0) * (expense.count.toDoubleOrNull() ?: 0.0)
        val totalText = if (total == 0.0) "0" else if (total % 1.0 == 0.0) total.toInt().toString() else String.format(Locale.getDefault(), "%.1f", total)
        val moneyColor = if (total > 0) PrimaryGreen else Color.DarkGray
        Text(
            text = "¥$totalText",
            modifier = Modifier.weight(1.2f),
            color = moneyColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

