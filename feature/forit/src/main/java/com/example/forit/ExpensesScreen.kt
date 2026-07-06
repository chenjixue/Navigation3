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
fun ExpensesScreen(
    selectedDate: String,
    peopleExpenses: List<PeopleExpenseResource>,
    otherExpenses: List<OtherExpenseResource>,
    onSavePeopleExpense: (List<PeopleExpenseResource>) -> Unit,
    onDeletePeopleExpense: (List<String>) -> Unit,
    onSaveOtherExpense: (List<OtherExpenseResource>) -> Unit,
    onDeleteOtherExpense: (List<String>) -> Unit,
) {
    val peopleKeys = remember { mutableSetOf<String>() }
    val peopleItems = remember { mutableStateListOf<PeopleExpenseUiState>() }
    val editingPeopleKeys = remember { mutableStateOf(setOf<String>()) }
    var peopleKeyToEditAfterLoad by remember { mutableStateOf<String?>(null) }

    val otherKeys = remember { mutableSetOf<String>() }
    val otherItems = remember { mutableStateListOf<OtherExpenseUiState>() }
    val editingOtherKeys = remember { mutableStateOf(setOf<String>()) }
    var otherKeyToEditAfterLoad by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedDate) {
        editingPeopleKeys.value = emptySet()
        editingOtherKeys.value = emptySet()
    }

    LaunchedEffect(peopleExpenses, selectedDate) {
        if (editingPeopleKeys.value.isNotEmpty()) {
            val newItemsMap = peopleExpenses.associateBy { it.key }
            val preservedList = peopleItems.mapNotNull { newItemsMap[it.key]?.asUiState() }.toMutableList()
            val preservedKeys = preservedList.map { it.key }.toSet()
            preservedList.addAll(peopleExpenses.filter { it.key !in preservedKeys }.map { it.asUiState() })
            peopleItems.clear()
            peopleItems.addAll(preservedList)
        } else {
            peopleItems.clear()
            peopleItems.addAll(peopleExpenses.map { it.asUiState() })
        }
        peopleKeys.clear()
        peopleItems.forEach { peopleKeys.add(it.key) }

        if (peopleKeyToEditAfterLoad != null) {
            editingPeopleKeys.value = editingPeopleKeys.value + peopleKeyToEditAfterLoad!!
            peopleKeyToEditAfterLoad = null
        }
    }

    LaunchedEffect(otherExpenses, selectedDate) {
        if (editingOtherKeys.value.isNotEmpty()) {
            val newItemsMap = otherExpenses.associateBy { it.key }
            val preservedList = otherItems.mapNotNull { newItemsMap[it.key]?.asUiState() }.toMutableList()
            val preservedKeys = preservedList.map { it.key }.toSet()
            preservedList.addAll(otherExpenses.filter { it.key !in preservedKeys }.map { it.asUiState() })
            otherItems.clear()
            otherItems.addAll(preservedList)
        } else {
            otherItems.clear()
            otherItems.addAll(otherExpenses.map { it.asUiState() })
        }
        otherKeys.clear()
        otherItems.forEach { otherKeys.add(it.key) }

        if (otherKeyToEditAfterLoad != null) {
            editingOtherKeys.value = editingOtherKeys.value + otherKeyToEditAfterLoad!!
            otherKeyToEditAfterLoad = null
        }
    }

    val maleWorkers = peopleItems.mapIndexedNotNull { index, item -> if (item.gender == "男") index to item else null }
    val femaleWorkers = peopleItems.mapIndexedNotNull { index, item -> if (item.gender == "女") index to item else null }

    val totalMale = maleWorkers.sumOf { it.second.price.toDoubleOrNull() ?: 0.0 }
    val totalFemale = femaleWorkers.sumOf { it.second.price.toDoubleOrNull() ?: 0.0 }
    val totalOther = otherItems.sumOf { (it.unitPrice.toDoubleOrNull() ?: 0.0) * (it.count.toDoubleOrNull() ?: 0.0) }
    val totalAll = totalMale + totalFemale + totalOther
    val totalAllText = if (totalAll == 0.0) "0" else if (totalAll % 1.0 == 0.0) totalAll.toInt().toString() else String.format(Locale.getDefault(), "%.1f", totalAll)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                if (editingPeopleKeys.value.isNotEmpty()) {
                    editingPeopleKeys.value = emptySet()
                    onSavePeopleExpense(peopleItems.map { it.asModel(selectedDate) })
                }
                if (editingOtherKeys.value.isNotEmpty()) {
                    editingOtherKeys.value = emptySet()
                    onSaveOtherExpense(otherItems.map { it.asModel(selectedDate) })
                }
            },
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 男工 Section
        item {
            Column {
                SectionHeaderWithAdd("男工", Color(0xFFE3F2FD), Color(0xFF1976D2)) {
                    val newItem = PeopleExpenseUiState(
                        key = generateUniqueKey(peopleKeys, selectedDate),
                        name = "",
                        price = "",
                        gender = "男"
                    )
                    val updatedList = peopleExpenses.toMutableList()
                    updatedList.add(newItem.asModel(selectedDate))
                    peopleKeyToEditAfterLoad = newItem.key
                    onSavePeopleExpense(updatedList)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("姓名", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Text("工价", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                }
            }
        }
        items(maleWorkers.size, key = { "male_${maleWorkers[it].second.key}" }) { idx ->
            val (originalIndex, item) = maleWorkers[idx]
            SwipeToDeletePeopleExpenseItem(
                expense = item,
                isEditing = editingPeopleKeys.value.contains(item.key),
                onClick = {
                    editingPeopleKeys.value = editingPeopleKeys.value + item.key
                },
                onValueChange = { updatedItem ->
                    val updateIndex = peopleItems.indexOfFirst { it.key == item.key }
                    if (updateIndex != -1) {
                        peopleItems[updateIndex] = updatedItem
                        onSavePeopleExpense(peopleItems.map { it.asModel(selectedDate) })
                    }
                },
                onDelete = {
                    val removeIndex = peopleItems.indexOfFirst { it.key == item.key }
                    if (removeIndex != -1) {
                        peopleItems.removeAt(removeIndex)
                        editingPeopleKeys.value = editingPeopleKeys.value - item.key
                        onDeletePeopleExpense(listOf(item.key))
                    }
                }
            )
        }

        // 女工 Section
        item {
            Column {
                SectionHeaderWithAdd("女工", Color(0xFFFCE4EC), Color(0xFFC2185B)) {
                    val newItem = PeopleExpenseUiState(
                        key = generateUniqueKey(peopleKeys, selectedDate),
                        name = "",
                        price = "",
                        gender = "女"
                    )
                    val updatedList = peopleExpenses.toMutableList()
                    updatedList.add(newItem.asModel(selectedDate))
                    peopleKeyToEditAfterLoad = newItem.key
                    onSavePeopleExpense(updatedList)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("姓名", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("工价", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                }
            }
        }
        items(femaleWorkers.size, key = { "female_${femaleWorkers[it].second.key}" }) { idx ->
            val (originalIndex, item) = femaleWorkers[idx]
            SwipeToDeletePeopleExpenseItem(
                expense = item,
                isEditing = editingPeopleKeys.value.contains(item.key),
                onClick = {
                    editingPeopleKeys.value = editingPeopleKeys.value + item.key
                },
                onValueChange = { updatedItem ->
                    val updateIndex = peopleItems.indexOfFirst { it.key == item.key }
                    if (updateIndex != -1) {
                        peopleItems[updateIndex] = updatedItem
                        onSavePeopleExpense(peopleItems.map { it.asModel(selectedDate) })
                    }
                },
                onDelete = {
                    val removeIndex = peopleItems.indexOfFirst { it.key == item.key }
                    if (removeIndex != -1) {
                        peopleItems.removeAt(removeIndex)
                        editingPeopleKeys.value = editingPeopleKeys.value - item.key
                        onDeletePeopleExpense(listOf(item.key))
                    }
                }
            )
        }

        // 其他开支 Section
        item {
            Column {
                SectionHeaderWithAdd("其他开支", Color.Transparent, Color.DarkGray) {
                    val newItem = OtherExpenseUiState(
                        key = generateUniqueKey(otherKeys, selectedDate),
                        name = "",
                        unitPrice = "",
                        count = ""
                    )
                    val updatedList = otherExpenses.toMutableList()
                    updatedList.add(newItem.asModel(selectedDate))
                    otherKeyToEditAfterLoad = newItem.key
                    onSaveOtherExpense(updatedList)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("项目名称", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Text("数量", modifier = Modifier.weight(1f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Text("单价", modifier = Modifier.weight(1.2f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Text("总额", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.End)
                }
            }
        }
        items(otherItems.size, key = { "other_${otherItems[it].key}" }) { index ->
            val item = otherItems[index]
            SwipeToDeleteOtherExpenseItem(
                expense = item,
                isEditing = editingOtherKeys.value.contains(item.key),
                onClick = {
                    editingOtherKeys.value = editingOtherKeys.value + item.key
                },
                onValueChange = { updatedItem ->
                    val updateIndex = otherItems.indexOfFirst { it.key == item.key }
                    if (updateIndex != -1) {
                        otherItems[updateIndex] = updatedItem
                        onSaveOtherExpense(otherItems.map { it.asModel(selectedDate) })
                    }
                },
                onDelete = {
                    val removeIndex = otherItems.indexOfFirst { it.key == item.key }
                    if (removeIndex != -1) {
                        otherItems.removeAt(removeIndex)
                        editingOtherKeys.value = editingOtherKeys.value - item.key
                        onDeleteOtherExpense(listOf(item.key))
                    }
                }
            )
        }

        // 合计
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("今日开支合计", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text("¥$totalAllText", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            }
        }
    }
}

@Composable
fun SectionHeaderWithAdd(title: String, tagColor: Color, tagTextColor: Color, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (tagColor != Color.Transparent) {
            Box(modifier = Modifier.background(tagColor, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(title, color = tagTextColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(title, color = tagTextColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onAddClick, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = PrimaryGreen)
        }
    }
}

@Composable
fun SwipeToDeletePeopleExpenseItem(
    expense: PeopleExpenseUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (PeopleExpenseUiState) -> Unit,
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
                        contentDescription = "删除",
                        tint = Color.White,
                    )
                }
            }
        },
    ) {
        PeopleExpenseCard(
            expense = expense,
            isEditing = isEditing,
            onClick = onClick,
            onValueChange = onValueChange,
        )
    }
}

@Composable
fun PeopleExpenseCard(
    expense: PeopleExpenseUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (PeopleExpenseUiState) -> Unit,
) {
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
        // Name
        if (isEditing) {
            BasicTextField(
                value = expense.name,
                onValueChange = { onValueChange(expense.copy(name = it)) },
                modifier = Modifier
                    .weight(1.5f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    if (expense.name.isEmpty()) {
                        Text("姓名", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (expense.name.isEmpty()) "姓名" else expense.name,
                modifier = Modifier.weight(1.5f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = if (expense.name.isEmpty()) TextGray else Color.Black
            )
        }

        // Price
        if (isEditing) {
            BasicTextField(
                value = expense.price,
                onValueChange = { onValueChange(expense.copy(price = it)) },
                modifier = Modifier
                    .weight(1.5f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    if (expense.price.isEmpty()) {
                        Text("工价", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (expense.price.isEmpty()) "¥0" else "¥${expense.price}",
                modifier = Modifier.weight(1.5f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = if (expense.price.isEmpty()) TextGray else PrimaryGreen
            )
        }
    }
}

@Composable
fun SwipeToDeleteOtherExpenseItem(
    expense: OtherExpenseUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (OtherExpenseUiState) -> Unit,
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
                        contentDescription = "删除",
                        tint = Color.White,
                    )
                }
            }
        },
    ) {
        OtherExpenseCard(
            expense = expense,
            isEditing = isEditing,
            onClick = onClick,
            onValueChange = onValueChange,
        )
    }
}

@Composable
fun OtherExpenseCard(
    expense: OtherExpenseUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (OtherExpenseUiState) -> Unit,
) {
    val total = (expense.unitPrice.toDoubleOrNull() ?: 0.0) * (expense.count.toDoubleOrNull() ?: 0.0)
    val totalText = if (total == 0.0) "0" else if (total % 1.0 == 0.0) total.toInt().toString() else String.format(Locale.getDefault(), "%.1f", total)

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
        // Name
        if (isEditing) {
            BasicTextField(
                value = expense.name,
                onValueChange = { onValueChange(expense.copy(name = it)) },
                modifier = Modifier
                    .weight(1.5f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    if (expense.name.isEmpty()) {
                        Text("项目名称", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (expense.name.isEmpty()) "项目名称" else expense.name,
                modifier = Modifier.weight(1.5f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = if (expense.name.isEmpty()) TextGray else Color.Black
            )
        }

        // Quantity
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
                        Text("数量", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (expense.count.isEmpty()) "0" else "${expense.count}",
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (expense.count.isEmpty()) TextGray else Color.Black
            )
        }

        // Unit Price
        if (isEditing) {
            BasicTextField(
                value = expense.unitPrice,
                onValueChange = { onValueChange(expense.copy(unitPrice = it)) },
                modifier = Modifier
                    .weight(1.2f)
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
                modifier = Modifier.weight(1.2f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (expense.unitPrice.isEmpty()) TextGray else Color.Black
            )
        }

        // Total
        val moneyColor = if (total > 0) PrimaryGreen else Color.DarkGray
        Text(
            text = "¥$totalText",
            modifier = Modifier.weight(1.5f),
            color = moneyColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

