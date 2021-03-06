package model.budget

import  model.json.KlaxonDate
import model.json.KlaxonRecurrence
import model.enums.Recurrence
import com.beust.klaxon.*
import model.core.DueDate
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BudgetItem @JvmOverloads constructor(
        @Json(name="scheduledAmount")
        var scheduledAmount: Double,
        @Json(name="actualAmount")
        var actualAmount: Double,
        @Json(name="due")
        @KlaxonDate
        var due: DueDate,
        @Json(name="recurrence")
        @KlaxonRecurrence
        var recurrence: Recurrence,
        @Json(name="name")
        var name: String,
        @Json(name="autopay")
        var autopay: Boolean,
        @Json(name="required")
        var required: Boolean,
        @Json(name="transferredToSavingsAccountName")
        var transferredToSavingsAccountName: String?,
        @Json(name="transferredToCreditAccountName")
        var transferredToCreditAccountName: String?,
        @Json(name="transferredToCreditAccountName")
        var dueDates: MutableList<DueDate>) : Serializable {

    override fun toString():String {
        var formattedNameStringBuilder = StringBuilder(name)
        for (nameLength in formattedNameStringBuilder.length..NAME_LENGTH){
            formattedNameStringBuilder.append(' ')
        }
        var scheduledAmountStringBuilder = StringBuilder(""+scheduledAmount)
        for (nameLength in scheduledAmountStringBuilder.length..AMOUNT_LENGTH){
            scheduledAmountStringBuilder.append(' ')
        }
        return "Due: ${due.dueDate.format(DateTimeFormatter.ISO_DATE)}  Name: ${formattedNameStringBuilder.substring(0, NAME_LENGTH)}\tScheduled: ${scheduledAmountStringBuilder}\tActual: ${due.amount}"
    }

    fun toNarrowString():String {
        return toNarrowString(due)
    }

    fun toNarrowString(date: LocalDate?): String {
        var formattedNameStringBuilder = StringBuilder(name)
        var autopayString = "<x>"
        if(!autopay){
            autopayString = "< >"
        }
        var requiredString = "{x}"
        if(!required){
            requiredString = "{ }"
        }
        for (nameLength in formattedNameStringBuilder.length..NAME_LENGTH){
            formattedNameStringBuilder.append(' ')
        }
        var scheduledAmountStringBuilder = StringBuilder(""+scheduledAmount)
        for (nameLength in scheduledAmountStringBuilder.length..AMOUNT_LENGTH){
            scheduledAmountStringBuilder.append(' ')
        }
        var transferString = "[x]"
        if(transferredToCreditAccountName.equals("null") && transferredToSavingsAccountName.equals("null")){
            transferString = "[ ]"
        }
        return "| ${autopayString}  | ${requiredString}\t| ${date}\t | ${formattedNameStringBuilder.substring(0, NAME_LENGTH)} | ${transferString}  | ${scheduledAmountStringBuilder} "
    }

    fun toNarrowString(date: DueDate?): String {
        var formattedNameStringBuilder = StringBuilder(name)
        var autopayString = "<x>"
        if(!autopay){
            autopayString = "< >"
        }
        var requiredString = "{x}"
        if(!required){
            requiredString = "{ }"
        }
        for (nameLength in formattedNameStringBuilder.length..NAME_LENGTH){
            formattedNameStringBuilder.append(' ')
        }
        var amountStringBuilder = StringBuilder(""+date?.amount)
        for (nameLength in amountStringBuilder.length..AMOUNT_LENGTH){
            amountStringBuilder.append(' ')
        }
        var transferString = "[x]"
        if(transferredToCreditAccountName.equals("null") && transferredToSavingsAccountName.equals("null")){
            transferString = "[ ]"
        }
        return "| ${autopayString}  | ${requiredString}\t| ${date?.dueDate}\t | ${formattedNameStringBuilder.substring(0, NAME_LENGTH)} | ${transferString}  | ${amountStringBuilder} "
    }

    fun serializeBudgetItemToJson(): String {
        var budgetStateStringBuilder = StringBuilder()
        budgetStateStringBuilder.append("{\n")
        budgetStateStringBuilder.append(String.format("\"%s\": %b,\n", AUTOPAY_KEY, autopay))
        budgetStateStringBuilder.append(String.format("\"%s\": %b,\n", REQUIRED_KEY, required))
        budgetStateStringBuilder.append(String.format("\"%s\": %.2f,\n", SCHEDULED_AMOUNT_KEY, scheduledAmount))
        budgetStateStringBuilder.append(String.format("\"%s\": %.2f,\n", ACTUAL_AMOUNT_KEY, actualAmount))
        budgetStateStringBuilder.append(String.format("\"%s\": %s,\n", DUE_KEY, dateConverter.toJson(due)))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", RECURRENCE_KEY, recurrence.name))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", NAME_KEY, name))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", SAVINGS_ACCOUNT_NAME_KEY, transferredToSavingsAccountName))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", CREDIT_ACCOUNT_NAME_KEY, transferredToCreditAccountName))
        budgetStateStringBuilder.append(String.format("\"%s\": %s\n", DUE_DATES_KEY, convertToDatesList(dueDates)))
        budgetStateStringBuilder.append("}\n")
        return budgetStateStringBuilder.toString()
    }

    private fun convertToDatesList(dueDates: MutableList<DueDate>): String? {
        var dueDatesStringBuilder = StringBuilder()
        dueDatesStringBuilder.append("[\n")
        dueDates.forEach { dueDate ->
            dueDatesStringBuilder.append(dateConverter.toJson(dueDate))
            dueDatesStringBuilder.append(',')
        }
        dueDatesStringBuilder = StringBuilder(dueDatesStringBuilder.substring(0,dueDatesStringBuilder.length-1))
        dueDatesStringBuilder.append("]\n")
        return dueDatesStringBuilder.toString()
    }

    fun fillOutDueDates() {
        when (recurrence) {
            Recurrence.DAILY, Recurrence.WEEKLY, Recurrence.BIWEEKLY, Recurrence.MONTHLY -> {
                if (dueDates.isEmpty()) {
                    dueDates.add(due)
                }
                val lastDueDate = dueDates.last()
                val oneYearFromToday = LocalDate.now().plusMonths(12).minusDays(1)
                if(lastDueDate.dueDate.isBefore(oneYearFromToday)) {
                    fillOutDueDatesRegularRecurrence()
                }
            }
        }
    }

    fun fillOutDueDatesRegularRecurrence(){
        var todaysDate: LocalDate = LocalDate.now()
        var pastDueDatesFinalIndex = -1
        for (dueDateIndex in 0..dueDates.size - 1) {
            val dueDate = dueDates.get(dueDateIndex)
            if (!todaysDate.isAfter(dueDate.dueDate)) {
                break
            } else {
                pastDueDatesFinalIndex = dueDateIndex
            }
        }
        if (pastDueDatesFinalIndex != -1) {
            if (pastDueDatesFinalIndex == dueDates.size - 1) {
                dueDates = ArrayList()
            } else {
                dueDates.subList(pastDueDatesFinalIndex, dueDates.size - 1)
            }
        }
        if (dueDates.isEmpty()) {
            dueDates.add(due)
        }
        var lastDueDate = dueDates.last().dueDate
        var amount = dueDates.last().amount
        val oneYearFromToday = todaysDate.plusMonths(12).minusDays(1)
        while (lastDueDate.isBefore(oneYearFromToday)) {
            when (recurrence) {
                Recurrence.WEEKLY -> {
                    lastDueDate = lastDueDate.plusWeeks(1)
                }
                Recurrence.BIWEEKLY -> {
                    lastDueDate = lastDueDate.plusWeeks(2)
                }
                Recurrence.MONTHLY -> {
                    lastDueDate = lastDueDate.plusMonths(1)
                }
                Recurrence.YEARLY -> {
                    lastDueDate = lastDueDate.plusYears(1)
                }
                else -> lastDueDate = oneYearFromToday
            }
            var newDueDate = DueDate(lastDueDate, amount)
            dueDates.add(newDueDate)
        }
    }

    fun validForBudgetState(budgetState: BudgetState): Boolean{
        var isValid = false
        if(dueDates.isEmpty()){
            when(recurrence) {
                Recurrence.DAILY, Recurrence.WEEKLY, Recurrence.BIWEEKLY, Recurrence.MONTHLY ->
                        fillOutDueDatesRegularRecurrence()
            }
        }
        if(dueDates.isEmpty()){
            dueDates.add(due)
        }
        dueDates.forEach {
            dueDate ->
            if (!isValid && dueDate.dueDate.isAfter(budgetState.startDate.minusDays(1)) &&
                    dueDate.dueDate.isBefore(budgetState.endDate.plusDays(1))) {
                isValid = true
            }
        }
        return isValid
    }

    fun validDueDateForBudgetState(budgetState: BudgetState): DueDate?{
        var validDueDate:DueDate? = null
        if(dueDates.isEmpty()){
            when(recurrence) {
                Recurrence.DAILY, Recurrence.WEEKLY, Recurrence.BIWEEKLY, Recurrence.MONTHLY ->
                    fillOutDueDatesRegularRecurrence()
            }
        }
        if(dueDates.isEmpty()){
            dueDates.add(due)
        }
        dueDates.forEach {
            dueDate ->
            if (validDueDate == null && dueDate.dueDate.isAfter(budgetState.startDate.minusDays(1)) &&
                    dueDate.dueDate.isBefore(budgetState.endDate.plusDays(1))) {
                validDueDate = dueDate
            }
        }
        return validDueDate
    }

    companion object {

        val ID_KEY = "id"
        val SCHEDULED_AMOUNT_KEY = "scheduledAmount"
        val ACTUAL_AMOUNT_KEY = "actualAmount"
        val AUTOPAY_KEY = "autopay"
        val REQUIRED_KEY = "required"
        val DUE_KEY = "due"
        val RECURRENCE_KEY = "recurrence"
        val NAME_KEY = "name"
        val SAVINGS_ACCOUNT_NAME_KEY = "transferToSavingsAccount"
        val CREDIT_ACCOUNT_NAME_KEY = "transferToCreditAccount"
        val DUE_DATES_KEY = "dueDates"
        val NAME_LENGTH=20
        val AMOUNT_LENGTH=6

        fun parseBudgetItemFromJsonObject(value: JsonObject?): BudgetItem? {
            val required: Boolean = value?.boolean(REQUIRED_KEY)!!
            val autopay: Boolean = value?.boolean(AUTOPAY_KEY)!!
            val scheduledAmount: Double = value?.double(SCHEDULED_AMOUNT_KEY)!!
            val actualAmount: Double = value?.double(ACTUAL_AMOUNT_KEY)!!
            val dueObj: JsonObject = value?.obj(DUE_KEY)!!
            val dueDate:DueDate? = dueDateStringParser(dueObj)
            val recurrence: Recurrence = Recurrence.valueOf(value!!.string(RECURRENCE_KEY)!!)
            val name: String = value?.string(NAME_KEY)!!
            var transferredToSavingsAccountName: String? = null
            if(value.containsKey(SAVINGS_ACCOUNT_NAME_KEY)) {
                transferredToSavingsAccountName = value?.string(SAVINGS_ACCOUNT_NAME_KEY)!!
            }
            var transferredToCreditAccountName: String? = null
            if(value.containsKey(SAVINGS_ACCOUNT_NAME_KEY)) {
                transferredToCreditAccountName = value?.string(CREDIT_ACCOUNT_NAME_KEY)!!
            }
            var dueDatesArray = value.array<JsonObject>(DUE_DATES_KEY)
            var dueDates = convertJsonArrayToListDueDate(dueDatesArray)
            var budgetItem = BudgetItem(scheduledAmount, actualAmount, dueDate!!, recurrence, name, autopay, required,
                    transferredToSavingsAccountName, transferredToCreditAccountName, dueDates)
            budgetItem.fillOutDueDates()
            return budgetItem
        }

        private fun convertJsonArrayToListDueDate(dueDatesArray: JsonArray<JsonObject>?): MutableList<DueDate> {
            var dueDates: MutableList<DueDate> = ArrayList<DueDate>()
            dueDatesArray?.forEach { dueDateObject ->
                dueDateStringParser(dueDateObject)?.let { dueDates.add(it) }
            }
            return dueDates
        }

        fun dateStringParser(dateString: String?): LocalDate{
            var localDate: LocalDate? = null
            if(dateString?.contains('.',true)!!){
                localDate =
                        LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")).toLocalDate()
            } else if(dateString?.contains('T', true)){
                localDate =
                        LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")).toLocalDate()
            } else {
                localDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
            return localDate
        }

        fun dueDateStringParser(dateObject: JsonObject?): DueDate?{
            var localDate: LocalDate? = null
            var localDateString:String = dateObject!!.string("dueDate")!!
            if (localDateString?.contains('.', true)!!) {
                localDate =
                        LocalDateTime.parse(localDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")).toLocalDate()
            } else if (localDateString?.contains('T', true)) {
                localDate =
                        LocalDateTime.parse(localDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")).toLocalDate()
            } else {
                localDate = LocalDate.parse(localDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
            var amount = dateObject.double("amount")
            return DueDate(localDate, amount!!)
        }

        val dateConverter = object: Converter {
            override fun canConvert(cls: Class<*>)
                    = cls == LocalDateTime::class.java

            override fun fromJson(jv: JsonValue) =
                    if (jv.string != null) {
                        dateStringParser(jv.string!!)
                    } else {
                        throw KlaxonException("Couldn't parse date: ${jv.string}")
                    }

            override fun toJson(o: Any) =
                    o.toString()

        }

        fun narrowStringHeader(): String {
            var formattedHeaderStringBuilder = StringBuilder()
            formattedHeaderStringBuilder.append("Choose | Auto | Req |     Due     |         Name         | Xfer |  Amount  ")
            return formattedHeaderStringBuilder.toString()
        }
    }

}
