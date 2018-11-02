package model

import java.time.LocalDateTime

data class BudgetAnalysisState(var checkingAccountBalance: Double? = 0.0,
                               var savingsAccountBalances: ArrayList<Double>? = ArrayList<Double>(),
                               var creditAccountBalances: MutableList<Double>? = ArrayList<Double>(),
                               var date: LocalDateTime? = LocalDateTime.now(),
                               var budgetItem: BudgetItem? = null) {

    constructor(applicationState: ApplicationState) : this() {
        this.checkingAccountBalance = applicationState.checkingAccount?.balance
        this.savingsAccountBalances = ArrayList()
        this.creditAccountBalances = ArrayList()
        applicationState.savingsAccounts?.forEach { savingsAccount -> savingsAccountBalances!!.add(savingsAccount.balance)}
        applicationState.creditAccounts?.forEach { creditAccount -> creditAccountBalances!!.add(creditAccount.balance)}
        this.date = LocalDateTime.now()
        this.budgetItem = null
    }

    override fun toString():String {
        return String.format("Budget Item Name: %s\t Amount: %.2f\tDate: %s\tChecking model.Account Balance: %.2f",
                budgetItem?.name, budgetItem?.actualAmount, date, checkingAccountBalance)
    }
}