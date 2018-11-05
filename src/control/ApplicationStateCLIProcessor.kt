package control

import model.*
import model.enums.Recurrence
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDateTime

class ApplicationStateCLIProcessor(var applicationState: ApplicationState, var applicationStateManager: ApplicationStateManager) {

    fun cliEntryPoint(stateFile: File): BudgetState? {
        var applicationStateBudgetAnalysis: ApplicationStateBudgetAnalysis = ApplicationStateBudgetAnalysis(applicationState)
        print("Would you like to modify your [0] Current Budget Items, [1] Future Budget States, [2] Savings Accounts, [3] Credit Accounts, [4] Checking model.Account, [5] Perform Current Week Analysis, [6] Perform Budget Analysis  or [7] Exit? ")
        print("\u001b[H\u001b[2J")
        var workingBudgetState: BudgetState? = null
        var option: Int = readLine()!!.toInt()
        when (option) {
            0 -> processCLIBudgetState()
            1 -> processFutureBudgetStates()
            2 -> processCLIAccount(true)
            3 -> processCLIAccount(false)
            4 -> processCLICheckingAccount()
            5 -> applicationStateBudgetAnalysis.performBudgetAnalysis(false)
            6 -> applicationStateBudgetAnalysis.performBudgetAnalysis(true)
            7 -> workingBudgetState = null
        }
        var applicationStateJson = applicationState?.serializeApplicationStateToJson()
        stateFile.bufferedWriter().use { out -> out.write(applicationStateJson) }
        return workingBudgetState
    }

    private fun processFutureBudgetStates() {
        print("Do you want to modify [0] unreconciled past or [1] current or [2] future? ")
        var option: Int = readLine()!!.toInt()
        processCLIBudgetItems(option)
    }

    fun processCLIBudgetState() {
        // Options [0] unreconciles [1] current [2] future
        print("Do you want to modify [0] unreconciled past or [1] current or [2] future? ")
        var option: Int = readLine()!!.toInt()
        processCLIBudgetItems(option)
    }

    private fun processCLIBudgetItems(temporalState: Int) {
        var budgetItems:MutableMap<String, BudgetItem>? = null
        when(temporalState){
            0 -> if(applicationState.pastUnreconciledBudgetItems != null) budgetItems = this!!.applicationState.pastUnreconciledBudgetItems!!
            1 -> if(applicationState.currentPayPeriodBudgetState?.currentBudgetItems != null) budgetItems = applicationState?.currentPayPeriodBudgetState?.currentBudgetItems!!
            2 -> if(applicationState.futureBudgetItems != null) budgetItems = applicationState.futureBudgetItems!!
        }

        print("Would you like to add a new budget item (y/n)? ")
        var addItemAnswer: Boolean = readLine()!!.toLowerCase().equals("y")
        if (addItemAnswer) {
            var budgetItem: BudgetItem = processCLICreateBudgetItem()
            budgetItems!!.put(budgetItem.name, budgetItem)
            when(temporalState){
                0 -> budgetItems?.put(budgetItem.name, budgetItem)
                1 -> {
                    if(!budgetItem.recurrence.equals(Recurrence.ONETIME)) {
                        val nextDueDate: LocalDateTime = determineNextDueLocalDateTime(budgetItem.recurrence,
                                budgetItem.due)
                        budgetItem = budgetItem.copy(due = nextDueDate)
                        val futureBudgetItem = applicationStateManager.updateFutureBudgetStatesWithNewBudgetItem(budgetItem)
                        applicationState.futureBudgetItems?.put(futureBudgetItem.name, futureBudgetItem)
                    }
                }
                2 -> {
                    val futureBudgetItem = applicationStateManager.updateFutureBudgetStatesWithNewBudgetItem(budgetItem)
                    budgetItems?.put(futureBudgetItem.name, futureBudgetItem)
                }
            }
        } else {
            var keysList: MutableList<String> = ArrayList<String>()
            for ((key, value) in budgetItems!!) {
                println(String.format("[%d]\t[%s]", keysList.size, value.toString()))
                keysList.add(key)
            }
            print(String.format("Since you would like to alter an existing budget item, which of the above budget items would you like to work with (0-%d)? ", keysList.size))
            var itemOption: Int = readLine()!!.toInt()
            var budgetItem = budgetItems!!.get(keysList.get(itemOption))
            if (budgetItem != null) {
                processCLIBudgetItemModificationOptions(budgetItem!!, temporalState)
            }
        }
    }

    private fun processCLICreateBudgetItem(): BudgetItem {
        print("What is the name of the item? ")
        var name: String = readLine()!!

        print("What is the recurrence of the future budget item [0] Daily, [1] Weekly, [2] Biweekly, [3] Monthly, [4] Yearly, [5] One-time? ")
        var recurrenceOption: Int = readLine()!!.toInt()
        var recurrence: Recurrence = Recurrence.values().get(recurrenceOption)

        print("What is the amount of the item? ")
        var amount: Double = readLine()!!.toDouble()

        print("What is the month of the item? ")
        var month: Int = readLine()!!.toInt()

        print("What is the day of the item? ")
        var day: Int = readLine()!!.toInt()

        print("What is the year of the item? ")
        var year: Int = readLine()!!.toInt()

        var due: LocalDateTime = LocalDateTime.of(year, month, day, 8, 0)

        return BudgetItem(applicationState.generateId(), amount, amount, due, recurrence, name)
    }

    private fun processCLIBudgetItemModificationOptions(budgetItem: BudgetItem, temporalState: Int) {
        //Options [1] add [2] modify [3] remove
        print("Do you want to [1] modify or [2] delete? ")
        var option: Int = readLine()!!.toInt()
        when (option) {
            1 -> processCLIModifyBudgetItem(budgetItem, temporalState)
            2 -> processCLIRemoveBudgetItem(budgetItem, temporalState)
        }
    }

    private fun processCLIRemoveBudgetItem(budgetItem: BudgetItem, temporalState: Int) {
        if (budgetItem != null) {
            when (temporalState){
                0 -> if(applicationState.pastUnreconciledBudgetItems != null)
                                                applicationState.pastUnreconciledBudgetItems!!.remove(budgetItem.name)
                1 -> if(applicationState.currentPayPeriodBudgetState?.currentBudgetItems != null)
                            applicationState.currentPayPeriodBudgetState?.currentBudgetItems!!.remove(budgetItem.name)
                2 -> if(applicationState.futureBudgetItems != null)
                            applicationState.futureBudgetItems!!.remove(budgetItem.name)
            }
        }
    }

    private fun processCLIModifyBudgetItem(budgetItem: BudgetItem, temporalState: Int) {

        print(String.format("What is the name of the item(%s)? ", budgetItem.name))
        var name = readLine()
        if (name.equals("")) {
            name = budgetItem.name
        }

        print(String.format("What is the recurrence of the future budget item [0] Daily, [1] Weekly, [2] Biweekly, [3] Monthly, [4] Yearly, [5] One-time (%s)? ", budgetItem.recurrence))
        var recurrenceOption = readLine()
        var recurrence: Recurrence? = null
        if (recurrenceOption.equals("")) {
            recurrence = budgetItem.recurrence
        } else {
            recurrence = Recurrence.values().get(recurrenceOption!!.toInt())
        }

        print(String.format("What is the scheduled amount of the item(%.2f)? ", budgetItem.scheduledAmount))
        var scheduledAmountResponse = readLine()
        var scheduledAmount: Double? = null
        if (scheduledAmountResponse.equals("")) {
            scheduledAmount = budgetItem.scheduledAmount
        } else {
            scheduledAmount = scheduledAmountResponse!!.toDouble()
        }

        print(String.format("What is the actual amount of the item(%.2f)? ", budgetItem.actualAmount))
        var actualAmountResponse = readLine()
        var actualAmount: Double? = null
        if (actualAmountResponse.equals("")) {
            actualAmount = budgetItem.actualAmount
        } else {
            actualAmount = actualAmountResponse!!.toDouble()
        }

        print(String.format("What is the month of the item(%d)? ", budgetItem.due.month.value))
        var monthResponse = readLine()
        var month: Int? = null
        if (monthResponse.equals("")) {
            month = budgetItem.due.month.value
        } else {
            month = monthResponse!!.toInt()
        }

        print(String.format("What is the day of the item(%d)? ", budgetItem.due.dayOfMonth))
        var dayResponse = readLine()
        var day: Int? = null
        if (dayResponse.equals("")) {
            day = budgetItem.due.dayOfMonth
        } else {
            day = dayResponse!!.toInt()
        }

        print(String.format("What is the year of the item(%d)? ", budgetItem.due.year))
        var yearResponse = readLine()
        var year: Int? = null
        if (yearResponse.equals("")) {
            year = budgetItem.due.year
        } else {
            year = yearResponse!!.toInt()
        }

        var due: LocalDateTime = LocalDateTime.of(year, month, day, 8, 0)

        var newBudgetItem: BudgetItem = BudgetItem(applicationState.generateId(), scheduledAmount,
                actualAmount, due, recurrence, name!!)

        when(temporalState){
            0 -> applicationState.pastUnreconciledBudgetItems?.let { updateBudgetItems(it, newBudgetItem) }
            1 -> applicationState.currentPayPeriodBudgetState?.currentBudgetItems?.let { updateBudgetItems(it, newBudgetItem) }
            2 -> applicationState.futureBudgetItems?.let { updateBudgetItems(it, newBudgetItem) }
        }
    }

    fun updateBudgetItems(budgetItems: MutableMap<String, BudgetItem>, budgetItem: BudgetItem){
        if(budgetItems != null) {
            if (budgetItems!!.contains(budgetItem.name)) {
                budgetItems!!.replace(budgetItem.name, budgetItem)
            } else {
                budgetItems!!.put(budgetItem.name, budgetItem)
            }
        }
    }


    fun processCLICheckingAccount() {
        if (applicationState?.checkingAccount != null) {
            print("Would you like to update the balance (y/n)? ")
            var updateCheckingBalance: Boolean = readLine()!!.toLowerCase().equals("y")
            if (updateCheckingBalance) {
                print(String.format("What is the new balance(%.2f)? ",
                        applicationState?.checkingAccount!!.balance))
                var newCurrentBalance = readLine()
                if (newCurrentBalance != null) {
                    applicationState?.checkingAccount!!.balance = newCurrentBalance.toDouble()
                }
            }
            if (applicationState.currentPayPeriodBudgetState?.currentBudgetItems != null) {
                var reconcilingCheckingAccount: Boolean = true
                while (reconcilingCheckingAccount) {
                    print("Would you like to reconcile budget items(y/n)? ")
                    reconcilingCheckingAccount = readLine()!!.toLowerCase().equals("y")
                    if (reconcilingCheckingAccount) {
                        var keysList: MutableList<String> = ArrayList<String>()
                        var itemIndex: Int = 0
                        for ((key, value) in applicationState.currentPayPeriodBudgetState?.currentBudgetItems!!) {
                            println(String.format("[%d]\t[%s]", itemIndex, value.toString()))
                            keysList.add(key)
                            itemIndex = itemIndex.inc()
                        }
                        print(String.format("Which of these current budget items would you like to reconcile[0-%d]? ", keysList.size - 1))
                        var itemOption: Int = readLine()!!.toInt()
                        var budgetItemKey = keysList.get(itemOption)
                        if (budgetItemKey != null) {
                            var reconciledBudgetItem: BudgetItem? =
                                    applicationState.currentPayPeriodBudgetState?.currentBudgetItems!!.remove(budgetItemKey)
                            if(reconciledBudgetItem != null) {
                                var reconciledAccountItem: AccountItem = AccountItem(reconciledBudgetItem.due,
                                        reconciledBudgetItem.name,
                                        reconciledBudgetItem.actualAmount)
                                applicationState?.checkingAccount!!
                                        .reconciledItems.add(reconciledAccountItem)
                            }
                        }
                    }
                }
            }
        }

    }

    fun processCLIAccount(isSavings: Boolean) {
        var accounts: MutableList<Account>
        if(isSavings){
            accounts = applicationState?.savingsAccounts!!
        } else {
            accounts = applicationState?.creditAccounts!!
        }
        if (accounts != null) {
            var accountList: MutableList<String> = ArrayList()
            if(accounts.size > 0) {
                for (account in accounts!!) {
                    println(String.format("[%d]\t[%s]", accountList.size, account.toString()))
                    accountList.add(account.name)
                }
                print(String.format("Which of the above accounts would you like to modify[0-%d,newline for new account]? ", accountList.size - 1))
            } else {
                print("To create a new account, you should hit return")
            }
            var accountIndexString: String? = readLine()
            if (accountIndexString != null && !accountIndexString.equals("")) {
                var accountIndex: Int = accountIndexString.toInt()
                var account: Account = accounts!!.get(accountIndex)
                processCLIUpdateAccount(account, isSavings)
            } else {
                processCLINewAccount(isSavings)
            }
        }
    }

    private fun processCLINewAccount(isSavingsAccount: Boolean) {
        print("What is the name of the account? ")
        var name = readLine()

        print("What is the initial balance? ")
        var initialBalance: Double = readLine()!!.toDouble()

        if(name != null && initialBalance != null) {
            var account: Account = Account(name!!, initialBalance, ArrayList(), ArrayList(), "")
            if(account != null){
                if(isSavingsAccount && applicationState?.savingsAccounts != null){
                    applicationState?.savingsAccounts!!.add(account)
                } else if(applicationState?.creditAccounts != null){
                    applicationState?.creditAccounts!!.add(account)
                }
            }
        }
    }

    private fun processCLIUpdateAccount(account: Account, isSavingsAccount: Boolean) {
        print("Would you like to [1] modify or [2] remove the current account? ")
        var updateOption: Int = readLine()!!.toInt()
        when(updateOption!!){
            1 -> processCLIModifyAccount(account)
            2 -> processCLIRemoveAccount(account, isSavingsAccount)
        }
    }

    private fun processCLIRemoveAccount(account: Account, isSavingsAccount: Boolean) {
        if(isSavingsAccount && applicationState?.savingsAccounts != null){
            applicationState?.savingsAccounts!!.remove(account)
        } else if(applicationState?.creditAccounts != null){
            applicationState?.creditAccounts!!.remove(account)
        }
    }

    private fun processCLIModifyAccount(account: Account) {
        if (account != null) {
            print("Would you like to update the balance (y/n)? ")
            var updateCheckingBalance: Boolean = readLine()!!.toLowerCase().equals("y")
            if (updateCheckingBalance) {
                print(String.format("What is the new balance(%.2f)? ", account!!.balance))
                var newCurrentBalance = readLine()
                if (newCurrentBalance != null) {
                    account!!.balance = newCurrentBalance.toDouble()
                }
            }
            if (account.items != null) {
                var reconcilingAccount: Boolean = true
                while (reconcilingAccount) {
                    print("Would you like to reconcile budget items(y/n)? ")
                    reconcilingAccount = readLine()!!.toLowerCase().equals("y")
                    if (reconcilingAccount) {
                        var itemIndex: Int = 0
                        for (accountItem in account.items!!) {
                            println(String.format("[%d]\t[%s]", itemIndex, accountItem))
                            itemIndex = itemIndex.inc()
                        }
                        print(String.format("Which of these current budget items would you like to reconcile[0-%d]? ", account.items.size - 1))
                        var itemOption: Int = readLine()!!.toInt()
                        var reconciledItem: AccountItem? = account.items!!.removeAt(itemIndex)
                        if(reconciledItem != null) {
                            account!!.items.add(reconciledItem)
                        }
                    }
                }
            }
        }

    }

    companion object {

        fun determinePreviousDueLocalDateTime(dayOfWeek: DayOfWeek, currentDateTime: LocalDateTime): LocalDateTime {
            return when (dayOfWeek) {
                DayOfWeek.FRIDAY -> currentDateTime
                DayOfWeek.SATURDAY -> currentDateTime.minusDays(1)
                DayOfWeek.SUNDAY -> currentDateTime.minusDays(2)
                DayOfWeek.MONDAY -> currentDateTime.minusDays(3)
                DayOfWeek.TUESDAY -> currentDateTime.minusDays(4)
                DayOfWeek.WEDNESDAY -> currentDateTime.minusDays(5)
                DayOfWeek.THURSDAY -> currentDateTime.minusDays(6)
            }
        }

        fun determineNextDueLocalDateTime(futureBudgetItemRecurrence: Recurrence, due: LocalDateTime): LocalDateTime {
            return when (futureBudgetItemRecurrence) {
                Recurrence.DAILY -> due.plusDays(Recurrence.DAILY.intervalDaysOrMonths)
                Recurrence.WEEKLY -> due.plusDays(Recurrence.WEEKLY.intervalDaysOrMonths)
                Recurrence.BIWEEKLY -> due.plusDays(Recurrence.BIWEEKLY.intervalDaysOrMonths)
                Recurrence.MONTHLY -> due.plusMonths(Recurrence.MONTHLY.intervalDaysOrMonths)
                Recurrence.YEARLY -> due.plusMonths(Recurrence.YEARLY.intervalDaysOrMonths)
                Recurrence.ONETIME -> due.plusMonths(0L)
            }
        }

    }
}