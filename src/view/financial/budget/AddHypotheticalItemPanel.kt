package view.financial.budget;

import control.handlers.configuration.TaskConfigurationHandler
import model.financial.budget.BudgetAnalysisState
import model.enums.budget.Recurrence
import model.financial.account.Account
import model.financial.budget.BudgetItem
import model.tasks.Task
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import utils.DateTimeUtils
import view.abstracts.BudgetPanel
import view.financial.items.ItemConfigurationPanel
import java.time.LocalDate

class AddHypotheticalItemPanel(width: Int, height: Int, var uiComponents: ApplicationUIComponents) :
        BudgetPanel(width, height, uiComponents.applicationState), TaskConfigurationHandler {


    var scheduledAmount = 0.0
    var actualAmount = 0.0
    var due: LocalDate = DateTimeUtils.currentDate()
    var name = "PLACEHOLDER"

    override fun build() {
        panel = uiComponents.budgetViewScreen?.yearlyBudgetPanel?.panel?.let { Positions.create(0,1).relativeToBottomOf(it) }?.let {
            Components.panel()
                .wrapWithBox(true)
                    .withTitle("Hypothetical Budgeting")
                .wrapWithShadow(false)
                .withSize(Sizes.create(width,height-2))
                .withPosition(it)
                .build()
        }
        var itemConfigurationPanel =  ItemConfigurationPanel(name, LocalDate.now(), false, false,
                scheduledAmount, actualAmount, Recurrence.ONETIME, width-25, height-4,
                "null", "null", applicationState)
        itemConfigurationPanel.build()
        panel!!.addComponent(itemConfigurationPanel.panel!!)
        val addHypotheticalBudgetItemButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Add Budget Item")
                .withPosition(Positions.create(0,0).relativeToRightOf(itemConfigurationPanel.panel!!))
                .build()
        addHypotheticalBudgetItemButton.onMouseReleased { it ->
            var newCurrentItem = itemConfigurationPanel.generateItem()
            uiComponents.budgetViewScreen?.monthlyBudgetPanel?.fillOutPanel(newCurrentItem)
            uiComponents.budgetViewScreen?.yearlyBudgetPanel?.fillOutPanel(newCurrentItem)
        }
        panel!!.addComponent(addHypotheticalBudgetItemButton)
    }

    override fun update(budgetAnalysisState: BudgetAnalysisState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleConfigurationTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleClear() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
