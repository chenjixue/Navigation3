package com.example.domain

import com.example.data.repository.ChouhuRepository
import com.example.data.repository.ExpenseRepository
import com.example.data.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDayProfitUseCase @Inject constructor(
    private val chouhuRepository: ChouhuRepository,
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository,
) {
    operator fun invoke(date: String): Flow<Double> = combine(
        chouhuRepository.getChouhuResources(date),
        expenseRepository.getPeopleExpenseResources(date),
        expenseRepository.getOtherExpenseResources(date),
        saleRepository.getSaleResources(date)
    ) { chouhus, peopleExpenses, otherExpenses, sales ->
        val totalIncome = sales.sumOf { it.unitPrice * it.count }
        val totalChouhu = chouhus.sumOf { it.unitPrice * it.count }
        val totalExpense = peopleExpenses.sumOf { it.price } + otherExpenses.sumOf { it.unitPrice * it.count }

        totalIncome - totalChouhu - totalExpense
    }
}