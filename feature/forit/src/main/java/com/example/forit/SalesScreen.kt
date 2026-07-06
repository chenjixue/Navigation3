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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.model.NoSaleResource
import com.example.model.OtherExpenseResource
import com.example.model.PeopleExpenseResource
import com.example.model.SaleResource
import kotlin.random.Random

@Composable
fun SalesScreen(
    selectedDate: String,
    sales: List<SaleResource>,
    hoards: List<NoSaleResource>,
    chouhus: List<ChouhuResource>,
    peopleExpenses: List<PeopleExpenseResource>,
    otherExpenses: List<OtherExpenseResource>,
    viewModel: ForYouViewModel,
    onSaveSale: (List<SaleResource>) -> Unit,
    onSaveHoard: (List<NoSaleResource>) -> Unit,
    onDeleteHoard: (List<String>) -> Unit,
    onDeleteSale: (List<String>) -> Unit,
) {
    // We expect exactly 4 fixed items for levels 1, 2, 3, 4(杂货) in sales list
    val fixedLevels = listOf("1", "2", "3", "4")
    val saleItems = remember { mutableStateListOf<SaleUiState>() }
    // Instead of a single editing index, we track a set of keys that are currently being edited.
    val editingSaleKeys = remember { mutableStateOf(setOf<String>()) }
    
    var keyToEditAfterLoad by remember { mutableStateOf<String?>(null) }

    val hoardKeys = remember { mutableSetOf<String>() }
    val hoardItems = remember { mutableStateListOf<NoSaleUiState>() }
    val editingHoardKeys = remember { mutableStateOf(setOf<String>()) }
    var hoardKeyToEditAfterLoad by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedDate) {
        editingSaleKeys.value = emptySet()
        editingHoardKeys.value = emptySet()
    }

    LaunchedEffect(sales, selectedDate) {
        if (editingSaleKeys.value.isNotEmpty()) {
            val newItemsMap = sales.associateBy { it.key }
            val preservedList = saleItems.mapNotNull { newItemsMap[it.key]?.asUiState() }.toMutableList()
            val preservedKeys = preservedList.map { it.key }.toSet()
            preservedList.addAll(sales.filter { it.key !in preservedKeys }.map { it.asUiState() })
            saleItems.clear()
            saleItems.addAll(preservedList)
        } else {
            saleItems.clear()
            saleItems.addAll(sales.map { it.asUiState() })
            saleItems.sortBy { it.level }
        }
        
        if (keyToEditAfterLoad != null) {
            editingSaleKeys.value = editingSaleKeys.value + keyToEditAfterLoad!!
            keyToEditAfterLoad = null
        }
    }

    LaunchedEffect(hoards, selectedDate) {
        if (editingHoardKeys.value.isNotEmpty()) {
            val newItemsMap = hoards.associateBy { it.key }
            val preservedList = hoardItems.mapNotNull { old -> 
                newItemsMap[old.key]?.asUiState() ?: old 
            }.toMutableList()
            hoardItems.clear()
            hoardItems.addAll(preservedList)
        } else {
            hoardItems.clear()
            
            val currentKeys = mutableSetOf<String>()
            hoards.forEach { currentKeys.add(it.key) }
            
            fixedLevels.forEach { level ->
                val existing = hoards.find { it.level == level }
                if (existing != null) {
                    hoardItems.add(existing.asUiState())
                } else {
                    val newKey = generateUniqueKey(currentKeys, selectedDate)
                    hoardItems.add(
                        NoSaleUiState(
                            key = newKey,
                            level = level,
                            count = ""
                        )
                    )
                }
            }
        }
        
        if (hoardKeyToEditAfterLoad != null) {
            editingHoardKeys.value = editingHoardKeys.value + hoardKeyToEditAfterLoad!!
            hoardKeyToEditAfterLoad = null
        }
    }

    val totalIncome = saleItems.sumOf { (it.unitPrice.toDoubleOrNull() ?: 0.0) * (it.count.toDoubleOrNull() ?: 0.0) }
    val totalIncomeText = if (totalIncome == 0.0) "0" else if (totalIncome % 1.0 == 0.0) totalIncome.toInt().toString() else String.format(Locale.getDefault(), "%.1f", totalIncome)

    val totalHoard = hoardItems.sumOf { it.count.toDoubleOrNull()?.toInt() ?: 0 }

    val currentDayProfit by viewModel.currentDayProfit.collectAsStateWithLifecycle()
    val dayProfitText = if (currentDayProfit == 0.0) "0" else if (currentDayProfit % 1.0 == 0.0) currentDayProfit.toInt().toString() else String.format(Locale.getDefault(), "%.1f", currentDayProfit)

    val currentMonthProfit by viewModel.currentMonthProfit.collectAsStateWithLifecycle()
    val monthProfitText = if (currentMonthProfit == 0.0) "0" else if (currentMonthProfit % 1.0 == 0.0) currentMonthProfit.toInt().toString() else String.format(Locale.getDefault(), "%.1f", currentMonthProfit)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // Clicking outside of items will clear all edit states and trigger a sort.
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                if (editingSaleKeys.value.isNotEmpty()) {
                    editingSaleKeys.value = emptySet()
                    saleItems.sortBy { it.level }
                    onSaveSale(saleItems.map { it.asModel(selectedDate) })
                }
                if (editingHoardKeys.value.isNotEmpty()) {
                    editingHoardKeys.value = emptySet()
                    onSaveHoard(hoardItems.map { it.asModel(selectedDate) })
                }
            },
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.background(Color(0xFFE3F2FD), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("售卖记录", color = Color(0xFF1976D2), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                val currentKeys = saleItems.map { it.key }.toMutableSet()
                                val newKey = generateUniqueKey(currentKeys, selectedDate)
                                val newItem = SaleUiState(
                                    key = newKey,
                                    name = "",
                                    level = "1",
                                    unitPrice = "",
                                    count = ""
                                )
                                // We don't add to saleItems directly. We append to the current sales list
                                // and save it. The database will update and trigger LaunchedEffect(sales)
                                // which will reload saleItems and set up the new row.
                                val updatedSales = sales.toMutableList()
                                updatedSales.add(newItem.asModel(selectedDate))
                                
                                // Remember this key so that when LaunchedEffect fires, we can find it
                                // and automatically set it to edit mode.
                                keyToEditAfterLoad = newKey
                                
                                onSaveSale(updatedSales)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Sale", tint = Color(0xFF1976D2))
                        }
                    }
                    Text("总卖: ¥$totalIncomeText", color = Color(0xFF1976D2), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                

            }
        }

        items(saleItems.size, key = { "sale_${saleItems[it].key}" }) { index ->
            val item = saleItems[index]
            val isEditing = editingSaleKeys.value.contains(item.key)
            SwipeToDeleteSaleItem(
                expense = item,
                isEditing = isEditing,
                onClick = {
                    editingSaleKeys.value = editingSaleKeys.value + item.key
                },
                onValueChange = { updatedItem ->
                    val updateIndex = saleItems.indexOfFirst { it.key == item.key }
                    if (updateIndex != -1) {
                        saleItems[updateIndex] = updatedItem
                        onSaveSale(saleItems.map { it.asModel(selectedDate) })
                    }
                },
                onDelete = {
                    val removeIndex = saleItems.indexOfFirst { it.key == item.key }
                    if (removeIndex != -1) {
                        saleItems.removeAt(removeIndex)
                        editingSaleKeys.value = editingSaleKeys.value - item.key
                        onDeleteSale(listOf(item.key))
                    }
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
                    Text("总数量: $totalHoard 斤", color = Color(0xFFD32F2F), fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                                        isEditing = editingHoardKeys.value.contains(item.key),
                                        onClick = {
                                            editingHoardKeys.value = editingHoardKeys.value + item.key
                                        },
                                        onValueChange = { updatedItem ->
                                            val updateIndex = hoardItems.indexOfFirst { it.key == item.key }
                                            if (updateIndex != -1) {
                                                hoardItems[updateIndex] = updatedItem
                                                onSaveHoard(hoardItems.map { it.asModel(selectedDate) })
                                            }
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
                Column {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("今日盈利", fontSize = 16.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("今日售卖 - 今日抽户 - 今日开支", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        Text(
                            text = "¥$dayProfitText",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                    
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.2f)))
                    
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("当月盈利", fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("当月售卖 - 当月抽户 - 当月开支", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                        Text(
                            text = "¥$monthProfitText",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun CompactHoardItem(
    expense: NoSaleUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (NoSaleUiState) -> Unit,
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
            .clickable { 
                if (!isEditing) onClick() 
            }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Level
        Box(
            modifier = Modifier
                .weight(1.2f)
                .background(tagBg, RoundedCornerShape(4.dp))
                .padding(horizontal = 2.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(title, color = tagText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        
        // Count
        if (isEditing) {
            BasicTextField(
                value = expense.count,
                onValueChange = { onValueChange(expense.copy(count = it)) },
                modifier = Modifier
                    .weight(1.5f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
fun SwipeToDeleteSaleItem(
    expense: SaleUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (SaleUiState) -> Unit,
    onDelete: () -> Unit,
) {
    val density = LocalDensity.current
    val minDragDistancePx = with(density) { 120.dp.toPx() }

    class StateHolder {
        var state: SwipeToDismissBoxState? = null
    }
    val holder = remember { StateHolder() }

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { totalDistance -> totalDistance * 0.6f },
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                val offset = try {
                    holder.state?.requireOffset() ?: 0f
                } catch (e: Exception) {
                    0f
                }
                
                if (offset > -minDragDistancePx) {
                    return@rememberSwipeToDismissBoxState false
                }
                
                onDelete()
                true
            } else {
                false
            }
        },
    )
    holder.state = dismissState

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFD32F2F), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("删除", color = Color.White, fontWeight = FontWeight.Bold)
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除记录",
                        tint = Color.White,
                    )
                }
            }
        },
    ) {
        CompactSaleItemRow(
            expense = expense,
            isEditing = isEditing,
            onClick = onClick,
            onValueChange = onValueChange,
        )
    }
}

@Composable
fun CompactSaleItemRow(
    expense: SaleUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (SaleUiState) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { 
                if (!isEditing) onClick() 
            }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: Level
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(tagBg, RoundedCornerShape(6.dp))
                .clickable {
                    if (!isEditing) {
                        onClick() // Enable editing
                    }
                    expanded = true // And show dropdown
                }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(title, color = tagText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("1", "2", "3", "4").forEach { levelOption ->
                    val optionTitle = if (levelOption == "4") "杂货" else "${levelOption}级货"
                    DropdownMenuItem(
                        text = { Text(optionTitle) },
                        onClick = {
                            onValueChange(expense.copy(level = levelOption))
                            expanded = false
                        }
                    )
                }
            }
        }
        
        // Row 2: Name and Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Name
            if (isEditing) {
                BasicTextField(
                    value = expense.name,
                    onValueChange = { onValueChange(expense.copy(name = it)) },
                    modifier = Modifier
                        .weight(1.3f)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                    decorationBox = { innerTextField ->
                        if (expense.name.isEmpty()) {
                            Text("名字", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }
                        innerTextField()
                    }
                )
            } else {
                Text(
                    text = if (expense.name.isEmpty()) "名字" else expense.name,
                    modifier = Modifier.weight(1.3f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = if (expense.name.isEmpty()) TextGray else Color.Black
                )
            }

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
        }

        // Row 3: Unit Price and Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unit Price
            if (isEditing) {
                BasicTextField(
                    value = expense.unitPrice,
                    onValueChange = { onValueChange(expense.copy(unitPrice = it)) },
                    modifier = Modifier
                        .weight(1f)
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
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = if (expense.unitPrice.isEmpty()) TextGray else Color.Black
                )
            }

            // Total
            val total = (expense.unitPrice.toDoubleOrNull() ?: 0.0) * (expense.count.toDoubleOrNull() ?: 0.0)
            val totalText = if (total == 0.0) "0" else if (total % 1.0 == 0.0) total.toInt().toString() else String.format(Locale.getDefault(), "%.1f", total)
            val moneyColor = if (total > 0) PrimaryGreen else Color.DarkGray
            
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "¥$totalText",
                    color = moneyColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

