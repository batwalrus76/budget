package control.handlers.view

import control.handlers.core.BaseHandler
import java.time.LocalDate

interface BaseViewHandler: BaseHandler {

    fun handleDay(date: LocalDate)
    fun handleWeek(startDate: LocalDate)
    fun handleMonth(startDate: LocalDate)
}