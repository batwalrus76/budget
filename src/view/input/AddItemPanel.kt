package view.input

import model.state.ApplicationState
import model.budget.BudgetAnalysisState
import model.core.DueDate
import model.enums.Recurrence
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import utils.DateTimeUtils
import view.BudgetPanel
import view.items.ItemConfigurationPanel
import java.time.LocalDate

class AddItemPanel(width: Int, height: Int, var uiComponents: ApplicationUIComponents,
                   applicationState: ApplicationState):
                BudgetPanel(width, height, applicationState){

    var scheduledAmount = 0.0
    var actualAmount = 0.0
    var due:LocalDate = DateTimeUtils.currentDate()
    var name = "PLACEHOLDER"

    override fun build() {
        panel = Components.panel()
            .wrapWithBox(false)
            .wrapWithShadow(false)
            .withSize(Sizes.create(width,height-1))
            .withPosition(Positions.create(0,1))
            .build()
        var itemConfigurationPanel =  ItemConfigurationPanel(name, LocalDate.now(), false, false,
                scheduledAmount, actualAmount, Recurrence.ONETIME, width-25, height-12,
                "null", "null", applicationState)
        itemConfigurationPanel.build()
        panel!!.addComponent(itemConfigurationPanel.panel!!)
        val addCurrentItemButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Add Current Item")
                .withPosition(Positions.create(0,0).relativeToRightOf(itemConfigurationPanel.panel!!))
                .build()
        addCurrentItemButton.onMouseReleased { it ->
            var newCurrentItem = itemConfigurationPanel.generateItem()
            applicationState.budgetItems?.set(newCurrentItem.name, newCurrentItem)
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        panel!!.addComponent(addCurrentItemButton)
        val addFutureItemButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Add Future Item")
                .withPosition(Positions.create(0,1).relativeToBottomOf(addCurrentItemButton))
                .build()
        addFutureItemButton.onMouseReleased {
            mouseAction ->
            var newFutureItem = itemConfigurationPanel.generateItem()
            newFutureItem.fillOutDueDates()
            applicationState.budgetItems?.set(name, newFutureItem)
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        panel!!.addComponent(addFutureItemButton)
        val addUnreconciledItemButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Add Unreconciled")
                .withPosition(Positions.create(0,1).relativeToBottomOf(addFutureItemButton))
                .build()
        addUnreconciledItemButton.onMouseReleased {
            mouseAction ->
            var newFutureItem = itemConfigurationPanel.generateItem()
            newFutureItem.fillOutDueDates()
            applicationState.pastUnreconciledBudgetItems?.set(name, newFutureItem)
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        panel!!.addComponent(addUnreconciledItemButton)
    }

    override fun update(budgetAnalysisState: BudgetAnalysisState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}