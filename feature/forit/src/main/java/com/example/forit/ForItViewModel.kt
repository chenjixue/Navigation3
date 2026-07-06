package com.example.forit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.data.repository.ChouhuRepository
import com.example.data.repository.ExpenseRepository
import com.example.data.repository.SaleRepository
import com.example.data.repository.UserDataRepository
import com.example.model.ChouhuResource
import com.example.model.NoSaleResource
import com.example.model.OtherExpenseResource
import com.example.model.PeopleExpenseResource
import com.example.model.SaleResource
import com.example.domain.GetDayProfitUseCase
import com.example.domain.GetMonthProfitUseCase
import kotlinx.coroutines.flow.combine

@HiltViewModel
class ForYouViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val chouhuRepository: ChouhuRepository,
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository,
    private val userDataRepository: UserDataRepository,
    private val getDayProfitUseCase: GetDayProfitUseCase,
    private val getMonthProfitUseCase: GetMonthProfitUseCase,
) : ViewModel() {

    val userData: StateFlow<String> = userDataRepository.userData
        .map { it.selectedDate.ifEmpty { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
        )

    val chouhuList: StateFlow<List<ChouhuResource>> = userData
        .flatMapLatest { selectedDate ->
            chouhuRepository.getChouhuResources(selectedDate)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val peopleExpenseList: StateFlow<List<PeopleExpenseResource>> = userData
        .flatMapLatest { selectedDate ->
            expenseRepository.getPeopleExpenseResources(selectedDate)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val otherExpenseList: StateFlow<List<OtherExpenseResource>> = userData
        .flatMapLatest { selectedDate ->
            expenseRepository.getOtherExpenseResources(selectedDate)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val saleExpenseList: StateFlow<List<SaleResource>> = userData
        .flatMapLatest { selectedDate ->
            saleRepository.getSaleResources(selectedDate)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val noSaleList: StateFlow<List<NoSaleResource>> = userData
        .flatMapLatest { selectedDate ->
            saleRepository.getNoSaleResources(selectedDate)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val currentDayProfit: StateFlow<Double> = userData
        .flatMapLatest { selectedDate ->
            getDayProfitUseCase(selectedDate)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0,
        )

    val currentMonthProfit: StateFlow<Double> = userData
        .flatMapLatest { selectedDate ->
            val yearMonth = selectedDate.substringBeforeLast("-") // Extracts "yyyy-MM" from "yyyy-MM-dd"
            getMonthProfitUseCase(yearMonth)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0,
        )

    fun deleteChouhu(keys: List<String>) {
        viewModelScope.launch {
            chouhuRepository.deleteChouhuResources(keys)
        }
    }

    fun deletePeopleExpenseResources(keys: List<String>) {
        viewModelScope.launch {
            expenseRepository.deletePeopleExpenseResources(keys)
        }
    }

    fun deleteOtherExpenseResources(keys: List<String>) {
        viewModelScope.launch {
            expenseRepository.deleteOtherExpenseResources(keys)
        }
    }

    fun deleteSaleResources(keys: List<String>) {
        viewModelScope.launch {
            saleRepository.deleteSaleResources(keys)
        }
    }

    fun deleteNoSaleResources(keys: List<String>) {
        viewModelScope.launch {
            saleRepository.deleteNoSaleResources(keys)
        }
    }

    fun updateSelectedDate(date: String) {
        viewModelScope.launch {
            userDataRepository.setSelectedDate(date)
        }
    }

    fun updateChouhuResources(chouhuResources: List<ChouhuResource>) {
        viewModelScope.launch {
            chouhuRepository.setChouhuResources(chouhuResources)
        }
    }

    fun updatePeopleExpenseResources(peopleExpenseResources: List<PeopleExpenseResource>) {
        viewModelScope.launch {
            expenseRepository.setPeopleExpenseResources(peopleExpenseResources)
        }
    }

    fun updateOtherExpenseResources(otherExpenseResources: List<OtherExpenseResource>) {
        viewModelScope.launch {
            expenseRepository.setOtherExpenseResources(otherExpenseResources)
        }
    }

    fun updateSaleResources(saleResources: List<SaleResource>) {
        viewModelScope.launch {
            saleRepository.setSaleResources(saleResources)
        }
    }

    fun updateNoSaleResources(noSaleResources: List<NoSaleResource>) {
        viewModelScope.launch {
            saleRepository.setNoSaleResources(noSaleResources)
        }
    }


}
