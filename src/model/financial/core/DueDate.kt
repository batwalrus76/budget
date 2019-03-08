package model.financial.core

import java.time.LocalDate

class DueDate(var dueDate: LocalDate, var amount: Double) {

    override fun toString(): String {
        return "{\"dueDate\":\"$dueDate\", \"amount\":$amount}"
    }
}