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

val PrimaryGreen = Color(0xFF3B6B22)
val BackgroundColor = Color(0xFFF9F9F6)
val TextGray = Color(0xFF666666)

data class SupplierItemUiState(
    val key: String,
    val name: String,
    val unitPrice: String,
    val quantity: String,
)

fun SupplierItemUiState.asModel(selectedDate: String) = ChouhuResource(
    key = key,
    name = name,
    unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
    count = quantity.toIntOrNull() ?: 0,
    dataTime = selectedDate,
)

fun ChouhuResource.asUiState() = SupplierItemUiState(
    key = key,
    name = name,
    unitPrice = if (unitPrice == 0.0) "" else if (unitPrice % 1.0 == 0.0) unitPrice.toInt().toString() else unitPrice.toString(),
    quantity = if (count == 0) "" else count.toString(),
)

data class PeopleExpenseUiState(
    val key: String,
    val name: String,
    val price: String,
    val gender: String,
)

fun PeopleExpenseUiState.asModel(selectedDate: String) = PeopleExpenseResource(
    key = key,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    gender = gender,
    dataTime = selectedDate,
)

fun PeopleExpenseResource.asUiState() = PeopleExpenseUiState(
    key = key,
    name = name,
    price = if (price == 0.0) "" else if (price % 1.0 == 0.0) price.toInt().toString() else price.toString(),
    gender = gender,
)

data class OtherExpenseUiState(
    val key: String,
    val name: String,
    val unitPrice: String,
    val count: String,
)

fun OtherExpenseUiState.asModel(selectedDate: String) = OtherExpenseResource(
    key = key,
    name = name,
    unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
    count = count.toIntOrNull() ?: 0,
    dataTime = selectedDate,
)

fun OtherExpenseResource.asUiState() = OtherExpenseUiState(
    key = key,
    name = name,
    unitPrice = if (unitPrice == 0.0) "" else if (unitPrice % 1.0 == 0.0) unitPrice.toInt().toString() else unitPrice.toString(),
    count = if (count == 0) "" else count.toString(),
)

data class SaleUiState(
    val key: String,
    val name: String,
    val level: String,
    val unitPrice: String,
    val count: String,
)

fun SaleUiState.asModel(selectedDate: String) = SaleResource(
    key = key,
    name = name,
    level = level,
    unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
    count = count.toIntOrNull() ?: 0,
    dataTime = selectedDate,
)

fun SaleResource.asUiState() = SaleUiState(
    key = key,
    name = name,
    level = level,
    unitPrice = if (unitPrice == 0.0) "" else if (unitPrice % 1.0 == 0.0) unitPrice.toInt().toString() else unitPrice.toString(),
    count = if (count == 0) "" else count.toString(),
)

data class NoSaleUiState(
    val key: String,
    val level: String,
    val count: String
)

fun NoSaleUiState.asModel(selectedDate: String) = NoSaleResource(
    key = key,
    level = level,
    count = count.toIntOrNull() ?: 0,
    dataTime = selectedDate,
)
fun NoSaleResource.asUiState() = NoSaleUiState(
    key = key,
    level = level,
    count = if (count == 0) "" else count.toString(),
)

fun generateUniqueKey(existingKeys: MutableSet<String>, selectedDate: String): String {
    var datePrefix = selectedDate.filter { it.isDigit() }
    if (datePrefix.isEmpty()) {
        datePrefix = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault()).format(java.util.Date())
    }
    var candidate: String
    do {
        // Generate a random integer, e.g., 6 digits
        val randomPart = kotlin.random.Random.nextInt(100000, 999999)
        candidate = "${datePrefix}_$randomPart"
    } while (existingKeys.contains(candidate)) // Check if it exists before adding
    existingKeys.add(candidate) // Add the newly generated unique key
    return candidate
}

fun shiftDateByDays(dateMillis: Long, dayOffset: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = dateMillis
        add(Calendar.DAY_OF_MONTH, dayOffset)
    }.timeInMillis
}



@Composable
fun ForItScreen(
    viewModel: ForYouViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("抽户", "开支", "售卖")
    val selectedDate by viewModel.userData.collectAsStateWithLifecycle()
    val chouhuList by viewModel.chouhuList.collectAsStateWithLifecycle()
    val peopleExpenseList by viewModel.peopleExpenseList.collectAsStateWithLifecycle()
    val otherExpenseList by viewModel.otherExpenseList.collectAsStateWithLifecycle()
    val saleExpenseList by viewModel.saleExpenseList.collectAsStateWithLifecycle()
    val noSaleList by viewModel.noSaleList.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
        TopHeader(
            selectedDateString = selectedDate,
            onDateChanged = viewModel::updateSelectedDate
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = PrimaryGreen,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = PrimaryGreen,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            fontSize = 16.sp,
                            color = if (selectedTabIndex == index) PrimaryGreen else Color.Gray,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> SuppliersScreen(
                    selectedDate = selectedDate,
                    initialItems = chouhuList,
                    onSave = viewModel::updateChouhuResources,
                    onDelete = { keys -> viewModel.deleteChouhu(keys) },
                )
                1 -> ExpensesScreen(
                    selectedDate = selectedDate,
                    peopleExpenses = peopleExpenseList,
                    otherExpenses = otherExpenseList,
                    onSavePeopleExpense = viewModel::updatePeopleExpenseResources,
                    onDeletePeopleExpense = viewModel::deletePeopleExpenseResources,
                    onSaveOtherExpense = viewModel::updateOtherExpenseResources,
                    onDeleteOtherExpense = viewModel::deleteOtherExpenseResources,
                )
                2 -> SalesScreen(
                    selectedDate = selectedDate,
                    sales = saleExpenseList,
                    hoards = noSaleList,
                    chouhus = chouhuList,
                    peopleExpenses = peopleExpenseList,
                    otherExpenses = otherExpenseList,
                    onSaveSale = viewModel::updateSaleResources,
                    onSaveHoard = viewModel::updateNoSaleResources,
                    onDeleteHoard = viewModel::deleteNoSaleExpenseResources,
                    onDeleteSale = viewModel::deleteSaleExpenseResources,
                )
            }
        }
    }
}

@Composable
fun TopHeader(
    selectedDateString: String,
    onDateChanged: (String) -> Unit
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Parses string like "2025-05-14" to millis
    val selectedDateMillis = remember(selectedDateString) {
        try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    val formattedDate = remember(selectedDateMillis) {
        SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(Date(selectedDateMillis))
    }

    if (showDatePicker) {
        val calendar = remember(selectedDateMillis) {
            Calendar.getInstance().apply {
                timeInMillis = selectedDateMillis
            }
        }

        DisposableEffect(context, showDatePicker, selectedDateMillis) {
            val dialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val newTime = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    val newDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(newTime))
                    onDateChanged(newDateString)
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.setOnDismissListener { showDatePicker = false }
            dialog.show()

            onDispose {
                dialog.dismiss()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryGreen)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("当前日期", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Text(formattedDate, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .clickable { 
                            val newTime = shiftDateByDays(selectedDateMillis, -1)
                            onDateChanged(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(newTime)))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .clickable { showDatePicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .clickable { 
                            val newTime = shiftDateByDays(selectedDateMillis, 1)
                            onDateChanged(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(newTime)))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

