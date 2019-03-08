package view.configuration.item

import control.handlers.configuration.ItemConfigurationHandler
import model.representation.state.ApplicationState
import model.financial.budget.BudgetAnalysisState
import model.enums.budget.Recurrence
import model.financial.budget.BudgetItem
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import utils.DateTimeUtils
import view.abstracts.BudgetPanel
import view.financial.items.ItemConfigurationPanel
import java.time.LocalDate

class AddItemPanel(width: Int, height: Int, var uiComponents: ApplicationUIComponents,
                   applicationState: ApplicationState, var position: Position = Positions.create(0,1),
                   var wrapWithBox:Boolean = false):
                BudgetPanel(width, height, applicationState), ItemConfigurationHandler{

    var scheduledAmount = 0.0
    var actualAmount = 0.0
    var due:LocalDate = DateTimeUtils.currentDate()
    var name = "PLACEHOLDER"

    override fun build() {
        panel = Components.panel()
            .wrapWithBox(wrapWithBox)
            .wrapWithShadow(false)
            .withSize(Sizes.create(width,height-1))
            .withPosition(position)
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

    override fun handleConfigurationItem(item: BudgetItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleClear() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}