package view.calendar

import model.budget.BudgetAnalysisState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import java.time.LocalDate

class CalendarDayPanel(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents,
                       var displayBox: Boolean = false, var position: Position, var showDayOfWeekLabel:Boolean = true,
                       var currentLocalDate:LocalDate = LocalDate.now()) {

    var panel: Panel? = null
    var dateLabel = Components.label()
            .withText(currentLocalDate.dayOfMonth.toString())
            .withSize(dateLabelSize)
            .withPosition(dateLabelPosition)
            .withBoxType(BoxType.BASIC)
            .build()
    var dateLabelPanel: Panel? = null
    var budgetAnalysisStatesPanel: Panel? = null
    var dayOfWeekLabel: Label? = null
    var headerLabel: Label? = null
    var dividerLabel: Label? = null
    var bottomDividerLabel: Label? = null
    var balanceLabel: Label? = null

    fun update(localDate: LocalDate, budgetAnalysisStates: MutableList<BudgetAnalysisState>){
        if(localDate != null && budgetAnalysisStates != null && budgetAnalysisStates.size > 0){
            currentLocalDate = localDate
            dateLabelPanel?.removeComponent(dateLabel!!)
            dateLabel = Components.label()
                    .withText(currentLocalDate.dayOfMonth.toString())
                    .withSize(dateLabelSize)
                    .withPosition(dateLabelPosition)
                    .withBoxType(BoxType.BASIC)
                    .build()
            dateLabelPanel?.addComponent(dateLabel!!)
            if(showDayOfWeekLabel) {
                dayOfWeekLabel = Components.label()
                        .withText(currentLocalDate.dayOfWeek.toString())
                        .withSize(Sizes.create(width - (dateLabel.width + 5), DAY_LABEL_HEIGHT))
                        .withPosition(Positions.create(1, 0).relativeToRightOf(dateLabel))
                        .withBoxType(BoxType.BASIC)
                        .build()
            }
            var availableLines = height - (DAY_LABEL_HEIGHT + 4)
            if(availableLines > 0) {
                budgetAnalysisStatesPanel!!.children.forEach { child -> budgetAnalysisStatesPanel!!.removeComponent(child) }
                addHeaderLabelsToPanel()
                availableLines = availableLines - 4
                var budgetAnalysisStatesRadioButtonGroup =
                        createBudgetAnalysisStatesRadioButtonGroup(budgetAnalysisStates, availableLines)
                budgetAnalysisStatesRadioButtonGroup?.let { budgetAnalysisStatesPanel!!.addComponent(it) }
                budgetAnalysisStatesRadioButtonGroup?.let{bottomDividerLabel = Components.label()
                        .withText(bottomDividerString.substring(0,Math.min(dividerString.length, width-3)))
                        .withSize(Sizes.create(width-3, 1))
                        .withPosition(Positions.create(0, 0).relativeToBottomOf(it))
                        .build()}
                budgetAnalysisStatesPanel!!.addComponent(bottomDividerLabel!!)
                var balanceString = String.format("%.2f",budgetAnalysisStates.last().checkingAccountBalance)
                var balancePadding = "                            ".substring(0, 27-balanceString.length)
                bottomDividerLabel?.let{balanceLabel = Components.label()
                        .withText(balancePadding+balanceString)
                        .withSize(Sizes.create(width-7, 1))
                        .withPosition(Positions.create(4, 0).relativeToBottomOf(it))
                        .build()}
                budgetAnalysisStatesPanel!!.addComponent(balanceLabel!!)
            }
        }
    }

    fun createBudgetAnalysisStatesRadioButtonGroup(budgetAnalysisStates: MutableList<BudgetAnalysisState>,
                                                   availableLines: Int): RadioButtonGroup? {
        var budgetAnalysisStatesRadioButtonGroup: RadioButtonGroup? = null
        if(budgetAnalysisStates != null && budgetAnalysisStates.size > 0 &&
                    budgetAnalysisStatesPanel != null && availableLines > 0){
            budgetAnalysisStatesRadioButtonGroup = Components.radioButtonGroup()
                    .withPosition(Position.create(0,0).relativeToBottomOf(this!!.dividerLabel!!))
                    .withSize(Sizes.create(this.width-3, availableLines-6))
                    .build()
            var currentAvailableLines = availableLines-4
            budgetAnalysisStates.forEach { budgetAnalysisState ->
                if(currentAvailableLines <= 0){
                    return null
                } else {
                    var itemNameString = budgetAnalysisState.budgetItem!!.name + "              "
                    var budgetAnalysisItemFormattedNameStringBuilder = StringBuilder(itemNameString.substring(0,14))
                    var amount = budgetAnalysisState.budgetItem!!.dueDates.find { it.dueDate.equals(currentLocalDate) }?.amount
                    val amountString = String.format("%.2f       ",amount).substring(0,7)
                    val balanceString = String.format("%.2f        ",budgetAnalysisState.checkingAccountBalance)
                            .substring(0,9)
                    var budgetAnalysisText =
                            String.format("%s| %s | %s ", budgetAnalysisItemFormattedNameStringBuilder.toString(), amountString, balanceString)
                    budgetAnalysisStatesRadioButtonGroup
                            .addOption(budgetAnalysisItemFormattedNameStringBuilder.toString(), budgetAnalysisText)
                    currentAvailableLines = currentAvailableLines--
                }
            }
        }
        return budgetAnalysisStatesRadioButtonGroup
    }

    fun build(): CalendarDayPanel {
        panel = Components.panel()
                .wrapWithBox(displayBox) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(position).build()
        var dateLabelPanelBuilder = Components.panel()
                .withSize(Sizes.create(2, 1))
                .wrapWithShadow(false) // shadow can be added
                .withPosition(dateLabelPanelPosition)
        if(height > DAY_LABEL_HEIGHT+2 && width > DAY_LABEL_WIDTH+2){
            dateLabelPanelBuilder.withSize(Sizes.create(4, 3))
            dateLabelPanelBuilder.wrapWithBox(true) // panels can be wrapped in a box
        }
        dateLabelPanel = dateLabelPanelBuilder.build()
        dateLabelPanel!!.addComponent(dateLabel)
        if(showDayOfWeekLabel) {
            dayOfWeekLabel = Components.label()
                    .withText(currentLocalDate.dayOfWeek.toString())
                    .withSize(Sizes.create(width - (dateLabel.width + 5), DAY_LABEL_HEIGHT))
                    .withPosition(Positions.create(1, 0).relativeToRightOf(dateLabel))
                    .withBoxType(BoxType.BASIC)
                    .build()
        }
        var availableLines = height - (DAY_LABEL_HEIGHT + 6)
        budgetAnalysisStatesPanel = Components.panel()
                .withSize(Sizes.create(width-3, availableLines-3))
                .wrapWithShadow(false) // shadow can be added
                .withPosition(Positions.create(0, 0).relativeToBottomOf(dateLabelPanel!!))
                .build()
        panel!!.addComponent(dateLabelPanel!!)
        if(showDayOfWeekLabel) {
            panel!!.addComponent(dayOfWeekLabel!!)
        }
        budgetAnalysisStatesPanel?.let { panel!!.addComponent(it) }
        return this
    }

    fun addHeaderLabelsToPanel(){
        headerLabel = Components.label()
                .withText(headerString.substring(0,Math.min(headerString.length, width-3)))
                .withSize(Sizes.create(width-3, 1))
                .withPosition(Positions.create(0, 0))
                .build()
        budgetAnalysisStatesPanel!!.addComponent(headerLabel!!)
        dividerLabel = Components.label()
                .withText(dividerString.substring(0,Math.min(dividerString.length, width-3)))
                .withSize(Sizes.create(width-3, 1))
                .withPosition(Positions.create(0, 0).relativeToBottomOf(headerLabel!!))
                .build()
        budgetAnalysisStatesPanel!!.addComponent(dividerLabel!!)
    }

    companion object {
        val DAY_LABEL_WIDTH = 2
        val DAY_LABEL_HEIGHT = 1
        val dateLabelSize = Sizes.create(DAY_LABEL_WIDTH, DAY_LABEL_HEIGHT)
        val dateLabelPosition = Positions.create(0,0)
        val dateLabelPanelPosition = Positions.create(0,0)
        var headerString = "         Name      | Amount   | Balance "
        var dividerString = "    ---------------|----------|--------"
        var bottomDividerString = "    -----------------------------------"
    }
}