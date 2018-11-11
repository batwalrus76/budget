package model

import model.BudgetState.Companion.parseBudgetItemsMapFromJsonObject
import model.BudgetState.Companion.serializeMapBudgetItemstoJson
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.io.File
import java.io.StringReader
import kotlin.Charsets

data class ApplicationState(var checkingAccount: Account? = Account("Checking model.Account", 0.0,
        ArrayList(), ArrayList(), ""),
                            var savingsAccounts: MutableList<Account>? = ArrayList<Account>(),
                            var creditAccounts: MutableList<Account>? = ArrayList<Account>(),
                            var pastUnreconciledBudgetItems: MutableMap<String, BudgetItem>? = HashMap(),
                            var currentPayPeriodBudgetState: BudgetState? = BudgetState(),
                            var futureBudgetStates: MutableList<BudgetState> = ArrayList(),
                            var futureBudgetItems: MutableMap<String, BudgetItem>? = HashMap(),
                            var nonOneTimeBudgetItems: MutableMap<String, BudgetItem>,
                            var oneUpNumber: Int = 0)
                                : java.io.Serializable {



    fun serializeApplicationStateToJson(): String {
        var jsonStringBuilder = StringBuilder()
        jsonStringBuilder.append("{\n")
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                CHECKING_ACCOUNT_KEY,  checkingAccount?.serializeAccountToJson()))
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                SAVINGS_ACCOUNTS_KEY, serializeListAccountsToJson(savingsAccounts)))
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                CREDIT_ACCOUNTS_KEY, serializeListAccountsToJson(creditAccounts)))
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                PAST_UNRECONCILED_BUDGET_ITEMS_KEY, serializeMapBudgetItemstoJson(pastUnreconciledBudgetItems)))
        jsonStringBuilder.append(String.format("\"%s\": ", CURRENT_PAY_PERIOD_BUDGET_STATE_KEY))
        jsonStringBuilder.append(currentPayPeriodBudgetState?.serializeBudgetStateToJson())
        jsonStringBuilder.append(",\n")
        jsonStringBuilder.append(String.format("\"%s\": [\n", FUTURE_BUDGET_STATES_KEY))
        futureBudgetStates?.forEach {futureBudgetState -> jsonStringBuilder.append(String.format("%s\n", futureBudgetState.serializeBudgetStateToJson())) }
        jsonStringBuilder.append("],\n")
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                NON_ONETIME_BUDGET_ITEMS_KEY, serializeMapBudgetItemstoJson(nonOneTimeBudgetItems)))
        jsonStringBuilder.append(String.format("\"%s\": \n%s,\n",
                FUTURE_BUDGET_ITEMS_KEY, serializeMapBudgetItemstoJson(futureBudgetItems)))
        jsonStringBuilder.append("}")
        return jsonStringBuilder.toString()
    }

    fun serializeListAccountsToJson(accountsList: MutableList<Account>?) : String {
        var accountsListStringBuilder = StringBuilder()
        accountsListStringBuilder.append("[\n")
        accountsList?.forEach{account: Account -> accountsListStringBuilder.append(String.format("%s,\n", account.serializeAccountToJson()))}
        accountsListStringBuilder.append("]")
        return accountsListStringBuilder.toString()
    }

    fun generateId(): Int {
        oneUpNumber += 1
        return oneUpNumber
    }

    companion object {

        val CHECKING_ACCOUNT_KEY:String = "checkingAccount"
        val SAVINGS_ACCOUNTS_KEY:String = "savingsAccounts"
        val CREDIT_ACCOUNTS_KEY:String = "creditAccounts"
        val CURRENT_PAY_PERIOD_BUDGET_STATE_KEY = "curentPayPeriodBudgetState"
        val FUTURE_BUDGET_STATES_KEY = "futureBudgetStates"
        val PAST_UNRECONCILED_BUDGET_ITEMS_KEY:String = "pastUnreconciledBudgetItems"
        val FUTURE_BUDGET_ITEMS_KEY:String = "futureBudgetItems"
        val NON_ONETIME_BUDGET_ITEMS_KEY:String = "nonOneTimeBudgetItems"

        fun deserializeJsonToApplicationState(stateFile: File): ApplicationState {
            val applicationStateFromFile: String = stateFile.readText(Charsets.UTF_8)
            return parseApplicationState(applicationStateFromFile)!!
        }

        fun parseApplicationState(sourceString: String): ApplicationState {
            val parser: Parser = Parser.default()
            val applicationStateObj: JsonObject = parser.parse(StringReader(sourceString)) as JsonObject
            val checkingAccountObj: JsonObject = applicationStateObj.obj(CHECKING_ACCOUNT_KEY)!!
            var checkingAccount: Account = Account.parseAccountFromJsonObject(checkingAccountObj)
            val savingsAccountsJsonArray: JsonArray<JsonObject> = applicationStateObj.array(SAVINGS_ACCOUNTS_KEY)!!
            var savingsAccounts: MutableList<Account> = parseAccountsListFromJsonAray(savingsAccountsJsonArray)
            val creditAccountsJsonArray: JsonArray<JsonObject> = applicationStateObj.array(CREDIT_ACCOUNTS_KEY)!!
            var creditAccounts: MutableList<Account> = parseAccountsListFromJsonAray(creditAccountsJsonArray)
            var pastUnreconciledBudgetItems: MutableMap<String, BudgetItem>? = HashMap()
            if(applicationStateObj.containsKey(PAST_UNRECONCILED_BUDGET_ITEMS_KEY)){
                val pastUnreconciledBudgetItemsObj: JsonObject = applicationStateObj.obj(PAST_UNRECONCILED_BUDGET_ITEMS_KEY)!!
                if(pastUnreconciledBudgetItemsObj != null) {
                    pastUnreconciledBudgetItems = parseBudgetItemsMapFromJsonObject(pastUnreconciledBudgetItemsObj)
                }
            }
            val nonOneTimeBudgetItemsObj: JsonObject = applicationStateObj.obj(NON_ONETIME_BUDGET_ITEMS_KEY)!!
            var nonOneTimeBudgetItems: MutableMap<String, BudgetItem> =
                    parseBudgetItemsMapFromJsonObject(nonOneTimeBudgetItemsObj)
            val futureBudgetItemsObj: JsonObject = applicationStateObj.obj(FUTURE_BUDGET_ITEMS_KEY)!!
            var futureBudgetItems: MutableMap<String, BudgetItem> =
                    parseBudgetItemsMapFromJsonObject(futureBudgetItemsObj)
            val currentPayPeriodBudgetState: BudgetState? = parseBudgetStateFromJsonObject(applicationStateObj)
            val futureBudgetStates: MutableList<BudgetState> = parseFutureBudgetStateFromJsonObject(applicationStateObj)
            return ApplicationState(checkingAccount, savingsAccounts, creditAccounts, pastUnreconciledBudgetItems,
                    currentPayPeriodBudgetState, futureBudgetStates, nonOneTimeBudgetItems, futureBudgetItems)
        }

        fun parseAccountsListFromJsonAray(accountsJsonArray: JsonArray<JsonObject>): MutableList<Account> {
            val accountsList: MutableList<Account> = ArrayList()
            accountsJsonArray.forEach { jsonObject -> accountsList.add(Account.parseAccountFromJsonObject(jsonObject)) }
            return accountsList
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

