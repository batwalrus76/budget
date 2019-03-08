package model.representation.state

import model.financial.budget.BudgetState.Companion.serializeMapBudgetItemstoJson
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import control.calendar.CalendarParser
import model.enums.budget.AccountType
import model.financial.account.Account
import model.financial.budget.BudgetItem
import model.financial.budget.BudgetState
import model.financial.budget.BudgetState.Companion.parseBudgetItemsMapFromJsonObject
import net.fortuna.ical4j.model.Calendar
import java.io.File
import java.io.StringReader
import java.time.LocalDate
import kotlin.Charsets

data class ApplicationState(var checkingAccount: Account? = Account("Checking Account", 0.0, AccountType.Checking),
                            var savingsAccounts: MutableMap<String, Account>? = HashMap(),
                            var creditAccounts: MutableMap<String, Account>? = HashMap(),
                            var pastUnreconciledBudgetItems: MutableMap<String, BudgetItem>? = HashMap(),
                            var currentPayPeriodBudgetState: BudgetState? = BudgetState(),
                            var futureBudgetStates: MutableList<BudgetState> = ArrayList(),
                            var budgetItems: MutableMap<String, BudgetItem>? = HashMap(),
                            var nonWorkProjects: MutableList<String>? = ArrayList(),
                            var calendarFile: String? = null)
                                : java.io.Serializable {

    var calendar: Calendar? = null

    fun serializeApplicationStateToJson(): String {
        var jsonStringBuilder = StringBuilder()
        jsonStringBuilder.append("{\n")
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                CHECKING_ACCOUNT_KEY,  checkingAccount?.serializeAccountToJson()))
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                SAVINGS_ACCOUNTS_KEY, serializeMapAccountsToJson(savingsAccounts)))
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                CREDIT_ACCOUNTS_KEY, serializeMapAccountsToJson(creditAccounts)))
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                PAST_UNRECONCILED_BUDGET_ITEMS_KEY, serializeMapBudgetItemstoJson(pastUnreconciledBudgetItems)))
        jsonStringBuilder.append(String.format("\"%s\": ", CURRENT_PAY_PERIOD_BUDGET_STATE_KEY))
        jsonStringBuilder.append(currentPayPeriodBudgetState?.serializeBudgetStateToJson())
        jsonStringBuilder.append(",\n")
        jsonStringBuilder.append(String.format("\"%s\": [\n", FUTURE_BUDGET_STATES_KEY))
        futureBudgetStates?.forEach {futureBudgetState -> jsonStringBuilder.append(String.format("%s,\n", futureBudgetState.serializeBudgetStateToJson())) }
        jsonStringBuilder = StringBuilder(jsonStringBuilder.substring(0, jsonStringBuilder.length-2))
        jsonStringBuilder.append("],\n")
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                BUDGET_ITEMS_KEY, serializeMapBudgetItemstoJson(budgetItems)))
        jsonStringBuilder.append(String.format("\"%s\": [\n", NON_WORK_KEY))
        nonWorkProjects?.forEach {nonWorkProject -> jsonStringBuilder.append(String.format("\"%s\",",nonWorkProject)) }
        jsonStringBuilder = StringBuilder(jsonStringBuilder.substring(0,jsonStringBuilder.length-1))
        jsonStringBuilder.append("],\n")
        jsonStringBuilder.append(String.format("\"%s\":\"%s\"\n", CALENDAR_FILE_KEY,calendarFile))
        jsonStringBuilder.append("}")
        return jsonStringBuilder.toString()
    }

    fun serializeMapAccountsToJson(accountsList: MutableMap<String, Account>?) : String {
        var accountsMapStringBuilder = StringBuilder()
        accountsMapStringBuilder.append("{\n")
        accountsList?.forEach{accountName, account -> accountsMapStringBuilder.append(String.format("\"%s\" : %s,\n", accountName, account.serializeAccountToJson()))}
        accountsMapStringBuilder = StringBuilder(accountsMapStringBuilder.substring(0,accountsMapStringBuilder.length-2))
        accountsMapStringBuilder.append("}")
        return accountsMapStringBuilder.toString()
    }

    fun addSavingsAccount(savingsAccount: Account){
        this.savingsAccounts?.put(savingsAccount.name, savingsAccount)
    }

    fun deleteSavingsAccount(savingsAccount: Account){
        this.savingsAccounts?.remove(savingsAccount.name)
    }

    fun addCreditAccount(creditAccount: Account){
        this.creditAccounts?.put(creditAccount.name, creditAccount)
    }

    fun deleteCreditAccount(creditAccount: Account){
        this.creditAccounts?.remove(creditAccount.name)
    }

    fun findBudgetStateForLocalDate(localDate: LocalDate): BudgetState? {
        var appropriateBudgetState: BudgetState? = null
        if(currentPayPeriodBudgetState!!.isValidForDueDate(localDate)){
            appropriateBudgetState = currentPayPeriodBudgetState
        } else {
            futureBudgetStates.forEach { budgetState ->
                if(appropriateBudgetState == null && budgetState.isValidForDueDate(localDate)){
                    appropriateBudgetState = budgetState
                    return appropriateBudgetState
                }
            }
        }
        return appropriateBudgetState
    }

    companion object {

        val CHECKING_ACCOUNT_KEY:String = "checkingAccount"
        val SAVINGS_ACCOUNTS_KEY:String = "savingsAccounts"
        val CREDIT_ACCOUNTS_KEY:String = "creditAccounts"
        val CURRENT_PAY_PERIOD_BUDGET_STATE_KEY = "curentPayPeriodBudgetState"
        val FUTURE_BUDGET_STATES_KEY = "futureBudgetStates"
        val NON_WORK_KEY = "nonWorkProjects"
        val CALENDAR_FILE_KEY = "calendarFile"
        val PAST_UNRECONCILED_BUDGET_ITEMS_KEY:String = "pastUnreconciledBudgetItems"
        val BUDGET_ITEMS_KEY:String = "budgetItems"

        fun deserializeJsonToApplicationState(stateFile: File): ApplicationState {
            val applicationStateFromFile: String = stateFile.readText(Charsets.UTF_8)
            var applicationState: ApplicationState = parseApplicationState(applicationStateFromFile)!!
            applicationState.calendar = applicationState.calendarFile?.let { CalendarParser.parseCalendarFile(it) }
            var events = CalendarParser.retrieveDateCalendarEvents(LocalDate.now(), applicationState.calendar!!)
            return applicationState
        }

        fun parseApplicationState(sourceString: String): ApplicationState {
            val parser: Parser = Parser()
            val applicationStateObj: JsonObject = parser.parse(StringReader(sourceString)) as JsonObject
            val checkingAccountObj: JsonObject = applicationStateObj.obj(CHECKING_ACCOUNT_KEY)!!
            var checkingAccount: Account = Account.parseAccountFromJsonObject(checkingAccountObj)
                val savingsAccountsJsonObject: JsonObject = applicationStateObj.obj(SAVINGS_ACCOUNTS_KEY)!!
            var savingsAccounts: MutableMap<String, Account> = parseAccountsMapFromJsonObject(savingsAccountsJsonObject)
            val creditAccountsJsonObject: JsonObject = applicationStateObj.obj(CREDIT_ACCOUNTS_KEY)!!
            var creditAccounts: MutableMap<String, Account> = parseAccountsMapFromJsonObject(creditAccountsJsonObject)
            var pastUnreconciledBudgetItems: MutableMap<String, BudgetItem>? = HashMap()
            if(applicationStateObj.containsKey(PAST_UNRECONCILED_BUDGET_ITEMS_KEY)){
                val pastUnreconciledBudgetItemsObj: JsonObject = applicationStateObj.obj(PAST_UNRECONCILED_BUDGET_ITEMS_KEY)!!
                if(pastUnreconciledBudgetItemsObj != null) {
                    pastUnreconciledBudgetItems = parseBudgetItemsMapFromJsonObject(pastUnreconciledBudgetItemsObj)
                }
            }
            val budgetItemsObject: JsonObject = applicationStateObj.obj(BUDGET_ITEMS_KEY)!!
            var budgetItems: MutableMap<String, BudgetItem> =
                    parseBudgetItemsMapFromJsonObject(budgetItemsObject)
            val currentPayPeriodBudgetState: BudgetState? = parseBudgetStateFromJsonObject(applicationStateObj)
            val futureBudgetStates: MutableList<BudgetState> = parseFutureBudgetStateFromJsonObject(applicationStateObj)
            val nonWorkProjects: MutableList<String> = parseNonWorkProjects(applicationStateObj)
            val calendarFile: String? = applicationStateObj.string(CALENDAR_FILE_KEY)
            return ApplicationState(checkingAccount, savingsAccounts, creditAccounts, pastUnreconciledBudgetItems,
                    currentPayPeriodBudgetState, futureBudgetStates, budgetItems, nonWorkProjects, calendarFile)
        }

        private fun parseNonWorkProjects(applicationStateObj: JsonObject): MutableList<String> {
            val nonWorkProjects: MutableList<String> = ArrayList()
            val nonWorkProjectsObj: JsonArray<String> = applicationStateObj.array(NON_WORK_KEY)!!
            nonWorkProjectsObj.forEach { nonWorkProject ->  nonWorkProjects.add(nonWorkProject)}
            return nonWorkProjects
        }

        fun parseAccountsMapFromJsonObject(accountsJsonObject: JsonObject): MutableMap<String, Account> {
            val accountsMap: MutableMap<String, Account> = HashMap()
            accountsJsonObject.forEach {
                accountName, value -> accountsMap[accountName] = Account.parseAccountFromJsonObject(value as JsonObject)
            }
            return accountsMap
        }

        private fun parseFutureBudgetStateFromJsonObject(obj: JsonObject): MutableList<BudgetState> {
            val futurePayPeriodBudgetStatesObj: JsonArray<JsonObject>? = obj.array(FUTURE_BUDGET_STATES_KEY)
            var futureBudgetStates: MutableList<BudgetState> = ArrayList()
            futurePayPeriodBudgetStatesObj?.forEach {
                jsonObject -> BudgetState.deserializeFromJsonObject(jsonObject)?.let { futureBudgetStates.add(it) }
            }
            return futureBudgetStates
        }

        private fun parseBudgetStateFromJsonObject(obj: JsonObject): BudgetState? {
            val currentPayPeriodBudgetStateObj = obj.obj(CURRENT_PAY_PERIOD_BUDGET_STATE_KEY)
            return currentPayPeriodBudgetStateObj?.let {
                BudgetState.deserializeFromJsonObject(currentPayPeriodBudgetStateObj)
            }
        }
    }
}

