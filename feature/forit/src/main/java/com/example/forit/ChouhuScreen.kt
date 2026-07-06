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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
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
fun SuppliersScreen(
    selectedDate: String,
    initialItems: List<ChouhuResource>,
    onSave: (List<ChouhuResource>) -> Unit,
    onDelete: (List<String>) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val editingKeys = remember { mutableStateOf(setOf<String>()) }
    var keyToEditAfterLoad by remember { mutableStateOf<String?>(null) }
    val supplierKeys = remember { mutableSetOf<String>() }
    val supplierItems = remember {
        mutableStateListOf<SupplierItemUiState>()
    }
    LaunchedEffect(selectedDate) {
        editingKeys.value = emptySet()
    }

    LaunchedEffect(initialItems, selectedDate) {
        if (editingKeys.value.isNotEmpty()) {
            val newItemsMap = initialItems.associateBy { it.key }
            val preservedList = supplierItems.mapNotNull { newItemsMap[it.key]?.asUiState() }.toMutableList()
            val preservedKeys = preservedList.map { it.key }.toSet()
            preservedList.addAll(initialItems.filter { it.key !in preservedKeys }.map { it.asUiState() })
            supplierItems.clear()
            supplierItems.addAll(preservedList)
        } else {
            supplierItems.clear()
            supplierItems.addAll(initialItems.map { it.asUiState() })
        }
        
        supplierKeys.clear()
        supplierItems.forEach { supplierKeys.add(it.key) }
        
        if (keyToEditAfterLoad != null) {
            editingKeys.value = editingKeys.value + keyToEditAfterLoad!!
            keyToEditAfterLoad = null
        }
    }
    val filteredItems = supplierItems.mapIndexedNotNull { index, item ->
        if (searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true)) {
            index to item
        } else {
            null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) {
                    if (editingKeys.value.isNotEmpty()) {
                        editingKeys.value = emptySet()
                        onSave(supplierItems.map { it.asModel(selectedDate) })
                    }
                },
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("搜索户名...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                val totalChouhu = supplierItems.sumOf { (it.unitPrice.toDoubleOrNull() ?: 0.0) * (it.quantity.toDoubleOrNull() ?: 0.0) }
                val totalChouhuCount = supplierItems.sumOf { it.quantity.toIntOrNull() ?: 0 }
                val totalChouhuText = if (totalChouhu == 0.0) "0" else if (totalChouhu % 1.0 == 0.0) totalChouhu.toInt().toString() else String.format(Locale.getDefault(), "%.1f", totalChouhu)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("今日抽户列表", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Button(
                        onClick = {
                            val newItem = SupplierItemUiState(
                                key = generateUniqueKey(supplierKeys, selectedDate),
                                name = "",
                                unitPrice = "",
                                quantity = "",
                            )
                            val updatedList = initialItems.toMutableList()
                            updatedList.add(newItem.asModel(selectedDate))
                            keyToEditAfterLoad = newItem.key
                            onSave(updatedList)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("添加", fontSize = 12.sp, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("姓名", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Column(modifier = Modifier.weight(1.2f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$totalChouhuCount 斤", color = Color(0xFF1976D2), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("斤量", fontSize = 12.sp, color = TextGray)
                    }
                    Text("单价", modifier = Modifier.weight(1f), fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    Column(modifier = Modifier.weight(1.5f), horizontalAlignment = Alignment.End) {
                        Text("¥$totalChouhuText", color = Color(0xFF1976D2), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("总额", fontSize = 12.sp, color = TextGray)
                    }
                }
            }

            itemsIndexed(
                items = filteredItems,
                key = { _, item -> "chouhu_${item.second.key}" }
            ) { _, indexedItem ->
                val index = indexedItem.first
                val item = indexedItem.second
                SwipeToDeleteSupplierItem(
                    supplier = item,
                    isEditing = editingKeys.value.contains(item.key),
                    onClick = {
                        editingKeys.value = editingKeys.value + item.key
                    },
                    onValueChange = { updatedItem ->
                        val updateIndex = supplierItems.indexOfFirst { it.key == item.key }
                        if (updateIndex != -1) {
                            supplierItems[updateIndex] = updatedItem
                            onSave(supplierItems.map { it.asModel(selectedDate) })
                        }
                    },
                    onDelete = {
                        val removeIndex = supplierItems.indexOfFirst { it.key == item.key }
                        if (removeIndex != -1) {
                            supplierItems.removeAt(removeIndex)
                            editingKeys.value = editingKeys.value - item.key
                            onDelete(listOf(item.key))
                        }
                    },
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SwipeToDeleteSupplierItem(
    supplier: SupplierItemUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (SupplierItemUiState) -> Unit,
    onDelete: () -> Unit,
) {
    val density = LocalDensity.current
    val minDragDistancePx = with(density) { 120.dp.toPx() }

    class StateHolder {
        var state: SwipeToDismissBoxState? = null
    }
    val holder = remember { StateHolder() }

    val dismissState = rememberSwipeToDismissBoxState(
        // Require a clear swipe distance before confirming delete.
        positionalThreshold = { totalDistance -> totalDistance * 0.6f },
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                val offset = try {
                    holder.state?.requireOffset() ?: 0f
                } catch (e: Exception) {
                    0f
                }
                
                // For EndToStart, offset is negative. We require dragging at least 120dp
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
                        contentDescription = "删除抽户",
                        tint = Color.White,
                    )
                }
            }
        },
    ) {
        SupplierCard(
            supplier = supplier,
            isEditing = isEditing,
            onClick = onClick,
            onValueChange = onValueChange,
        )
    }
}

@Composable
fun SupplierCard(
    supplier: SupplierItemUiState,
    isEditing: Boolean,
    onClick: () -> Unit,
    onValueChange: (SupplierItemUiState) -> Unit,
) {
    val total = (supplier.unitPrice.toDoubleOrNull() ?: 0.0) * (supplier.quantity.toDoubleOrNull() ?: 0.0)
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
                value = supplier.name,
                onValueChange = { onValueChange(supplier.copy(name = it)) },
                modifier = Modifier
                    .weight(1.5f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    if (supplier.name.isEmpty()) {
                        Text("姓名", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (supplier.name.isEmpty()) "姓名" else supplier.name,
                modifier = Modifier.weight(1.5f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = if (supplier.name.isEmpty()) TextGray else Color.Black
            )
        }

        // Quantity
        if (isEditing) {
            BasicTextField(
                value = supplier.quantity,
                onValueChange = { onValueChange(supplier.copy(quantity = it)) },
                modifier = Modifier
                    .weight(1.2f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    if (supplier.quantity.isEmpty()) {
                        Text("斤量", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (supplier.quantity.isEmpty()) "0斤" else "${supplier.quantity}斤",
                modifier = Modifier.weight(1.2f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (supplier.quantity.isEmpty()) TextGray else Color.Black
            )
        }

        // Unit Price
        if (isEditing) {
            BasicTextField(
                value = supplier.unitPrice,
                onValueChange = { onValueChange(supplier.copy(unitPrice = it)) },
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    if (supplier.unitPrice.isEmpty()) {
                        Text("单价", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = if (supplier.unitPrice.isEmpty()) "¥0" else "¥${supplier.unitPrice}",
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (supplier.unitPrice.isEmpty()) TextGray else Color.Black
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



