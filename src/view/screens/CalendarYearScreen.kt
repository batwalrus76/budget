package view.screens

import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import view.calendar.CalendarYearPanel
import java.time.LocalDate

class CalendarYearScreen (var width: Int, var height: Int, var component: Component, var uiComponents: ApplicationUIComponents) {

    var panel: Panel? = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withTitle(TITLE)
            .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
            .withPosition(Positions.create(0,0).relativeToBottomOf(component))
            .build()

    var calendarYearPanel: CalendarYearPanel = CalendarYearPanel(width-2, height-2, uiComponents)

    fun update(selectedDate: LocalDate){
        calendarYearPanel.update(selectedDate)
    }

    fun build() {
        calendarYearPanel.build()
        calendarYearPanel.panel?.let { panel?.addComponent(it) }
    }


    companion object {
        val TITLE: String = "Calendar Year View"
    }
}