package view.temporal.calendar

import model.financial.budget.BudgetAnalysisState
import model.enums.view.View
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
import org.hexworks.zircon.api.kotlin.onSelection
import view.financial.items.ItemConfigurationPanel
import view.screens.BaseScreen
import view.screens.calendar.BaseCalendarScreen
import java.time.LocalDate

class CalendarDayPanel(width: Int, height: Int, uiComponents: ApplicationUIComponents,
                       displayBox: Boolean = false, position: Position, showDayOfWeekLabel:Boolean = true,
                       selectedLocalDate:LocalDate = LocalDate.now(),
                       var title:String = selectedLocalDate.dayOfWeek.toString(),
                       baseScreen: BaseScreen):
                    BaseCalendarPanel(width, height, uiComponents, displayBox, position, showDayOfWeekLabel,
                            selectedLocalDate, baseScreen){

    var dateLabelButton: Button? = null
    var budgetAnalysisStatesPanel: Panel? = null
    var dayOfWeekLabel: Label? = null
    var headerLabel: Label? = null
    var dividerLabel: Label? = null
    var bottomDividerLabel: Label? = null
    var balanceLabel: Label? = null
    var budgetAnalysisStates: MutableList<BudgetAnalysisState>? = null

    open fun update(localDate: LocalDate, budgetAnalysisStates: MutableList<BudgetAnalysisState>){
        this.budgetAnalysisStates = budgetAnalysisStates
        panel = Components.panel()
                .wrapWithBox(displayBox) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(position)
                .build()
        if(localDate != null && budgetAnalysisStates != null) {
            selectedLocalDate = LocalDate.of(localDate.year, localDate.month, localDate.dayOfMonth)
            dateLabelButton = Components.button()
                    .withText(selectedLocalDate.dayOfMonth.toString())
                    .withBoxType(BoxType.BASIC)
                    .build()
            dateLabelButton!!.onMouseReleased {
                uiComponents.updateDate(selectedLocalDate, View.CALENDAR_DAY)
            }
            this.dateLabelButton?.let { panel?.addComponent(it) }
            if (showDayOfWeekLabel) {
                title = selectedLocalDate.dayOfWeek.toString()
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

            budgetAnalysisStatesRadioButtonGroup!!.onSelection { it ->
                updateitemConfigurationPanel(it)
            }
            var currentAvailableLines = availableLines-3
            budgetAnalysisStates.forEach { budgetAnalysisState ->
                if(currentAvailableLines <= 1){
                    return null
                } else {
                    var itemNameString = budgetAnalysisState.budgetItem!!.name + "              "
                    var budgetAnalysisItemFormattedNameStringBuilder = StringBuilder(itemNameString.substring(0,14))
                    var amount = budgetAnalysisState.budgetItem!!.dueDates.find { it.dueDate.equals(selectedLocalDate) }?.amount
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


    private fun updateitemConfigurationPanel(selection: RadioButtonGroup.Selection) {
        if(baseScreen != null && baseScreen is BaseCalendarScreen){
            var baseCalendarScreen = baseScreen as BaseCalendarScreen

            val budgetItem = this.budgetAnalysisStates?.find { it.budgetItem!!.name == selection.key }?.budgetItem
            baseCalendarScreen.itemConfigurationPanel = budgetItem?.scheduledAmount?.let {
                baseCalendarScreen.buildItemConfigurationPanel(budgetItem.name, budgetItem.due.dueDate,
                        budgetItem.required, budgetItem.autopay, it,  budgetItem.actualAmount, budgetItem.recurrence)
            }
            baseCalendarScreen.itemConfigurationPanel?.build()
        }
    }

    override fun build() {
        super.build()
        this.dateLabelButton?.let { panel?.removeComponent(it) }
        dateLabelButton = Components.button()
                .withText(selectedLocalDate.dayOfMonth.toString())
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