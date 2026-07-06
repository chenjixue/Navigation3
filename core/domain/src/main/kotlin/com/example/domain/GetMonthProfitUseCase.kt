package com.example.domain

import com.example.data.repository.ChouhuRepository
import com.example.data.repository.ExpenseRepository
import com.example.data.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMonthProfitUseCase @Inject constructor(
    private val chouhuRepository: ChouhuRepository,
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository,
) {
    operator fun invoke(yearMonth: String): Flow<Double> = combine(
        chouhuRepository.getChouhuResources(),
        expenseRepository.getPeopleExpenseResources(),
        expenseRepository.getOtherExpenseResources(),
        saleRepository.getSaleResources()
    ) { chouhus, peopleExpenses, otherExpenses, sales ->
        val monthChouhus = chouhus.filter { it.dataTime.startsWith(yearMonth) }
        val monthPeopleExpenses = peopleExpenses.filter { it.dataTime.startsWith(yearMonth) }
        val monthOtherExpenses = otherExpenses.filter { it.dataTime.startsWith(yearMonth) }
        val monthSales = sales.filter { it.dataTime.startsWith(yearMonth) }

        val totalIncome = monthSales.sumOf { it.unitPrice * it.count }
        val totalChouhu = monthChouhus.sumOf { it.unitPrice * it.count }
        val totalExpense = monthPeopleExpenses.sumOf { it.price } + monthOtherExpenses.sumOf { it.unitPrice * it.count }

        totalIncome - totalChouhu - totalExpense
    }
}