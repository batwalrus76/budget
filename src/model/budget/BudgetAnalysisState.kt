package model.budget

import model.state.ApplicationState
import utils.DateTimeUtils
import java.time.LocalDate

data class BudgetAnalysisState(var checkingAccountBalance: Double? = 0.0,
                               var savingsAccountBalances: MutableMap<String, Double>? = HashMap(),
                               var creditAccountBalances: MutableMap<String, Double>? = HashMap(),
                               var date: LocalDate? = DateTimeUtils.currentDate(),
                               var budgetItem: BudgetItem? = null) {

    constructor(applicationState: ApplicationState) : this() {
        this.checkingAccountBalance = applicationState.checkingAccount?.balance
        this.savingsAccountBalances = HashMap()
        this.creditAccountBalances = HashMap()
        applicationState.savingsAccounts?.forEach {
            savingsAccountName, savingsAccount -> savingsAccountBalances!![savingsAccountName] = savingsAccount.balance
        }
        applicationState.creditAccounts?.forEach {
            creditAccountName, creditAccount -> creditAccountBalances!![creditAccountName] = creditAccount.balance
        }
        this.date = DateTimeUtils.currentDate()
        this.budgetItem = null
    }

    override fun toString():String {
        return String.format("Budget Item Name: %s\t Amount: %.2f\tDate: %s\tChecking model.account.Account Balance: %.2f",
                budgetItem?.name, budgetItem?.actualAmount, date, checkingAccountBalance)
    }

    fun copy(): BudgetAnalysisState {
        var checkingAccountBalance = (0.0).plus(this.checkingAccountBalance!!)
        var savingsAccountBalances: MutableMap<String, Double>? = HashMap()
        this.savingsAccountBalances?.forEach { key, value -> savingsAccountBalances!![key] = (0.0).plus(value)}
        var creditAccountBalances: MutableMap<String, Double>? = HashMap()
        this.creditAccountBalances?.forEach { key, value -> creditAccountBalances!![key] = (0.0).plus(value)}
        var date = LocalDate.of(this.date!!.year, this.date!!.month, this.date!!.dayOfMonth)
        return BudgetAnalysisState(checkingAccountBalance, savingsAccountBalances, creditAccountBalances, date, budgetItem)
    }

}