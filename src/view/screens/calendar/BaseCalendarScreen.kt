package view.screens.calendar

import model.enums.budget.Recurrence
import model.financial.budget.BudgetAnalysisState
import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Component
import view.financial.items.ItemConfigurationPanel
import view.screens.BaseScreen
import java.time.LocalDate

open class BaseCalendarScreen (width: Int, height: Int, var component: Component, uiComponents: ApplicationUIComponents,
                               var itemConfigurationWidth: Int = width/3, var itemConfigurationHeight:Int = height-3):
        BaseScreen(width, height, uiComponents){

    var itemConfigurationPanel: ItemConfigurationPanel? = null

    override fun build() {
        itemConfigurationPanel = buildItemConfigurationPanel("PLACEHOLDER", LocalDate.now(), false,
                false, 0.0, 0.0, Recurrence.ONETIME)
        if(itemConfigurationPanel != null) {
            itemConfigurationPanel!!.build()
            itemConfigurationPanel?.panel?.let { panel?.addComponent(it) }
        }
    }

    fun buildItemConfigurationPanel(name: String, localDate: LocalDate, isRequied: Boolean, isAutopay: Boolean,
                                    scheduledAmount: Double, actualAmount: Double,
                                    recurrence: Recurrence, targetSavingsAccountName: String = "null",
                                    targetCreditAccountName: String = "null"): ItemConfigurationPanel? {
        var temptemConfigurationPanel: ItemConfigurationPanel? = null
        if(this.panel != null) {
            temptemConfigurationPanel = ItemConfigurationPanel(name, localDate, isRequied, isAutopay,
                    scheduledAmount, actualAmount, recurrence, itemConfigurationWidth, itemConfigurationHeight,
                    targetSavingsAccountName, targetCreditAccountName, uiComponents.applicationState,
                    false, Positions.create(width-(itemConfigurationWidth+3),0),
                    true)
        }
        return temptemConfigurationPanel
    }

    open fun update(localDate: LocalDate, budgetAnalysisStates: MutableList<BudgetAnalysisState>?): BudgetState?{
        if(itemConfigurationPanel?.panel != null && panel?.children!!.contains(itemConfigurationPanel?.panel as Component)) {
            itemConfigurationPanel!!.panel?.let { panel?.removeComponent(it) }
        }
        if(itemConfigurationPanel == null || itemConfigurationPanel?.panel == null) {
            itemConfigurationPanel = buildItemConfigurationPanel("PLACEHOLDER", localDate, false,
                    false, 0.0, 0.0, Recurrence.ONETIME)
            itemConfigurationPanel?.build()
        }
        if(itemConfigurationPanel?.panel != null && !panel?.children!!.contains(itemConfigurationPanel?.panel as Component)) {
            itemConfigurationPanel?.panel?.let { panel?.addComponent(it) }
        }
        return super.update()
    }

    override fun update(): BudgetState? {
        return this.update(LocalDate.now(), null)
    }
}