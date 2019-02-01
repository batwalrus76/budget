package view.calendar

import model.budget.BudgetAnalysisState
import model.enums.View
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import java.time.LocalDate

class CalendarDayPanel(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents,
                       var displayBox: Boolean = false, var position: Position, var showDayOfWeekLabel:Boolean = true,
                       var currentLocalDate:LocalDate = LocalDate.now(), var title:String = currentLocalDate.dayOfWeek.toString()) {

    var panel: Panel? = null
    var dateLabelButton: Button? = null
    var budgetAnalysisStatesPanel: Panel? = null
    var dayOfWeekLabel: Label? = null
    var headerLabel: Label? = null
    var dividerLabel: Label? = null
    var bottomDividerLabel: Label? = null
    var balanceLabel: Label? = null

    fun update(localDate: LocalDate, budgetAnalysisStates: MutableList<BudgetAnalysisState>){
        panel = Components.panel()
                .wrapWithBox(displayBox) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(position)
                .build()
        if(localDate != null && budgetAnalysisStates != null) {
            currentLocalDate = LocalDate.of(localDate.year, localDate.month, localDate.dayOfMonth)
            dateLabelButton = Components.button()
                    .withText(currentLocalDate.dayOfMonth.toString())
                    .withBoxType(BoxType.BASIC)
                    .build()
            dateLabelButton!!.onMouseReleased {
                uiComponents.updateDate(currentLocalDate, View.CALENDAR_DAY)
            }
            this.dateLabelButton?.let { panel?.addComponent(it) }
            if (showDayOfWeekLabel) {
                title = currentLocalDate.dayOfWeek.toString()
                dayOfWeekLabel = Components.label()
                        .withText(title)
                        .withPosition(Positions.create(1, -1).relativeToRightOf(dateLabelButton!!))
                        .build()
                this.dayOfWeekLabel?.let{ panel?.addComponent(it)}
            }
            var availableLines = height - 3
            if (budgetAnalysisStates.size > 0 && availableLines > 4) {
                budgetAnalysisStatesPanel = Components.panel()
                        .withSize(Sizes.create(width - 3, availableLines - 3))
                        .wrapWithShadow(false) // shadow can be added
                        .withPosition(Positions.create(0, 0).relativeToBottomOf(dateLabelButton!!))
                        .build()
                budgetAnalysisStatesPanel?.let { panel!!.addComponent(it) }
                addHeaderLabelsToPanel()
                availableLines = availableLines - 4
                var budgetAnalysisStatesRadioButtonGroup =
                        createBudgetAnalysisStatesRadioButtonGroup(budgetAnalysisStates, availableLines)
                budgetAnalysisStatesRadioButtonGroup?.let { budgetAnalysisStatesPanel!!.addComponent(it) }
                budgetAnalysisStatesRadioButtonGroup?.let {
                    bottomDividerLabel = Components.label()
                            .withText(bottomDividerString.substring(0, Math.min(dividerString.length, width - 3)))
                            .withSize(Sizes.create(width - 3, 1))
                            .withPosition(Positions.create(0, 0).relativeToBottomOf(it))
                            .build()
                }
                bottomDividerLabel?.let { budgetAnalysisStatesPanel!!.addComponent(it) }
                var balanceString = String.format("%.2f", budgetAnalysisStates.last().checkingAccountBalance)
                var balancePadding = "                            ".substring(0, 27 - balanceString.length)
                bottomDividerLabel?.let {
                    balanceLabel = Components.label()
                            .withText(balancePadding + balanceString)
                            .withSize(Sizes.create(width - 7, 1))
                            .withPosition(Positions.create(4, 0).relativeToBottomOf(it))
                            .build()
                }
                balanceLabel?.let { budgetAnalysisStatesPanel!!.addComponent(it) }
            }
        }
    }

    fun createBudgetAnalysisStatesRadioButtonGroup(budgetAnalysisStates: MutableList<BudgetAnalysisState>,
                                                   availableLines: Int): RadioButtonGroup? {
        var budgetAnalysisStatesRadioButtonGroup: RadioButtonGroup? = null
        if(budgetAnalysisStates != null && budgetAnalysisStates.size > 0 &&
                    budgetAnalysisStatesPanel != null && availableLines > 2){
            var budgetAnalysisStatesRadioButtonGroupPositon = Position.create(0,0)
            if(this.dividerLabel != null) {
                budgetAnalysisStatesRadioButtonGroupPositon =
                        budgetAnalysisStatesRadioButtonGroupPositon.relativeToBottomOf(this!!.dividerLabel!!)
            }
            budgetAnalysisStatesRadioButtonGroup = Components.radioButtonGroup()
                    .withPosition(budgetAnalysisStatesRadioButtonGroupPositon)
                    .withSize(Sizes.create(this.width-3, availableLines-3))
                    .build()
            var currentAvailableLines = availableLines-3
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
        this.dateLabelButton?.let { panel?.removeComponent(it) }
        dateLabelButton = Components.button()
                .withText(currentLocalDate.dayOfMonth.toString())
                .withBoxType(BoxType.BASIC)
                .build()
        this.dateLabelButton?.let { panel?.addComponent(it) }
        if(showDayOfWeekLabel) {
            this.dayOfWeekLabel?.let { panel?.removeComponent(it) }
            dayOfWeekLabel = Components.label()
                    .withText(title)
                    .withPosition(Positions.create(1, -1).relativeToRightOf(dateLabelButton!!))
                    .build()
            panel!!.addComponent(dayOfWeekLabel!!)
        }
        return this
    }

    fun addHeaderLabelsToPanel(){
        val headerStringText = headerString.substring(0,Math.min(headerString.length, width-3))
        if(headerStringText.length > 9) {
            headerLabel = Components.label()
                    .withText(headerStringText)
                    .withSize(Sizes.create(width - 3, 1))
                    .withPosition(Positions.create(0, 0))
                    .build()
            budgetAnalysisStatesPanel!!.addComponent(headerLabel!!)
        }
        val dividerStringLabel = dividerString.substring(0,Math.min(dividerString.length, width-3))
        if(dividerStringLabel.length > 9) {
            dividerLabel = Components.label()
                    .withText(dividerStringLabel)
                    .withSize(Sizes.create(width - 3, 1))
                    .withPosition(Positions.create(0, 0).relativeToBottomOf(headerLabel!!))
                    .build()
            budgetAnalysisStatesPanel!!.addComponent(dividerLabel!!)
        }
    }

    companion object {
        val DAY_LABEL_WIDTH = 2
        val DAY_LABEL_HEIGHT = 1
        var headerString = "         Name      | Amount   | Balance "
        var dividerString = "    ---------------|----------|--------"
        var bottomDividerString = "    -----------------------------------"
    }
}