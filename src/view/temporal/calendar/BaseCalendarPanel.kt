package view.temporal.calendar

import control.handlers.view.CalendarViewHandler
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import view.screens.BaseScreen
import java.time.LocalDate

abstract class BaseCalendarPanel(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents,
                                 var displayBox: Boolean = false,
                                 var position: Position, var showDayOfWeekLabel:Boolean = true,
                                 var selectedLocalDate:LocalDate = LocalDate.now(),
                                 val baseScreen: BaseScreen): CalendarViewHandler {

    var panel = Components.panel()
            .wrapWithBox(displayBox)
            .wrapWithShadow(false)
            .withSize(Sizes.create(this.width, this.height))
            .withPosition(position)
            .build()

    open fun build(){

    }

    override fun handleDay(date: LocalDate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleWeek(startDate: LocalDate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleMonth(startDate: LocalDate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}