package com.example.forit

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import kotlin.math.abs
import kotlin.random.Random

// 全局常量
val inputPadding = 4.dp
val inputFontSize = 14.sp

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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.background(Color(0xFFE3F2FD), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("今日抽户列表", color = Color(0xFF1976D2), fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
        
        FloatingActionButton(
            onClick = {
                val newItem = SupplierItemUiState(
                    key = generateUniqueKey(supplierKeys, selectedDate),
                    name = "",
                    unitPrice = "",
                    quantity = "",
                )
                supplierItems.add(newItem)
                keyToEditAfterLoad = newItem.key
                onSave(supplierItems.map { it.asModel(selectedDate) })
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PrimaryGreen,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Chouhu")
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
    val dismissThresholdPx = with(density) { 120.dp.toPx() }
    val directionConfirmThresholdPx = with(density) { 15.dp.toPx() }
    val autoDeleteThresholdPx = with(density) { 50.dp.toPx() }
    val dragFactor = 0.5f

    var offsetX by remember { mutableFloatStateOf(0f) }
    var dragDirectionConfirmed by remember { mutableStateOf(false) }
    var hasTriggeredDelete by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFFD32F2F))
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX.dp)
                .pointerInput(Unit) {
                    var totalDragX = 0f
                    var totalDragY = 0f

                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        totalDragX = 0f
                        totalDragY = 0f
                        hasTriggeredDelete = false
                        dragDirectionConfirmed = false

                        do {
                            val event = awaitPointerEvent()
                            val changes = event.changes

                            if (changes.size == 1) {
                                val change = changes.first()
                                val dragX = change.position.x - change.previousPosition.x
                                val dragY = change.position.y - change.previousPosition.y

                                totalDragX += dragX
                                totalDragY += dragY

                                val absTotalX = abs(totalDragX)
                                val absTotalY = abs(totalDragY)

                                if (!dragDirectionConfirmed) {
                                    if (absTotalX > directionConfirmThresholdPx || absTotalY > directionConfirmThresholdPx) {
                                        if (absTotalX > absTotalY) {
                                            dragDirectionConfirmed = true
                                            change.consume()
                                        } else {
                                            break
                                        }
                                    }
                                } else {
                                    val newOffset = offsetX + dragX * dragFactor
                                    offsetX = newOffset.coerceIn(-dismissThresholdPx, 0f)
                                    change.consume()

                                    if (!hasTriggeredDelete && offsetX < -autoDeleteThresholdPx) {
                                        hasTriggeredDelete = true
                                        onDelete()
                                        offsetX = 0f
                                        break
                                    }
                                }
                            }
                        } while (changes.any { it.pressed } && !hasTriggeredDelete)

                        if (!hasTriggeredDelete && dragDirectionConfirmed) {
                            offsetX = 0f
                        }
                        dragDirectionConfirmed = false
                    }
                }
        ) {
            SupplierCard(
                supplier = supplier,
                isEditing = isEditing,
                onClick = onClick,
                onValueChange = onValueChange,
            )
        }
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
        val commonTextStyle = LocalTextStyle.current.copy(fontSize = inputFontSize, textAlign = TextAlign.Center)
        
        if (isEditing) {
            BasicTextField(
                value = supplier.name,
                onValueChange = { onValueChange(supplier.copy(name = it)) },
                modifier = Modifier
                    .weight(1.5f)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                    .padding(inputPadding),
                textStyle = commonTextStyle,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (supplier.name.isEmpty()) {
                            Text("姓名", color = TextGray, style = commonTextStyle)
                        }
                        innerTextField()
                    }
                }
            )
        } else {
            Text(
                text = if (supplier.name.isEmpty()) "姓名" else supplier.name,
                modifier = Modifier.weight(1.5f),
                fontSize = inputFontSize,
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
                    .padding(inputPadding),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = commonTextStyle,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (supplier.quantity.isEmpty()) {
                            Text("斤量", color = TextGray, style = commonTextStyle)
                        }
                        innerTextField()
                    }
                }
            )
        } else {
            Text(
                text = if (supplier.quantity.isEmpty()) "0斤" else "${supplier.quantity}斤",
                modifier = Modifier.weight(1.2f),
                fontSize = inputFontSize,
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
                    .padding(inputPadding),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = commonTextStyle,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        if (supplier.unitPrice.isEmpty()) {
                            Text("单价", color = TextGray, style = commonTextStyle)
                        }
                        innerTextField()
                    }
                }
            )
        } else {
            Text(
                text = if (supplier.unitPrice.isEmpty()) "¥0" else "¥${supplier.unitPrice}",
                modifier = Modifier.weight(1f),
                fontSize = inputFontSize,
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
            fontSize = inputFontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}



