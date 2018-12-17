package view.input

import model.budget.BudgetAnalysisState
import model.budget.BudgetItem
import model.budget.BudgetItem.Companion.dateStringParser
import model.core.DueDate
import model.state.ApplicationState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.TextArea
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onKeyStroke
import org.hexworks.zircon.api.kotlin.onMouseReleased
import view.BudgetPanel
import kotlin.properties.Delegates

class SelectedDueDatePanel(width: Int, height: Int, var uiComponents: ApplicationUIComponents,
                           applicationState: ApplicationState, var component:Component,
                           var budgetItem: BudgetItem):
                            BudgetPanel(width, height, applicationState) {

    var firstButton: Button? = null
    var previousButton: Button? = null
    var nextButton: Button? = null
    var lastButton: Button? = null
    var dueDateText: String = ""
    var dueDateLabel: Label? = null
    var dueDateTextArea: TextArea? = null
    var dueDateAmountText: String = "0.0"
    var dueDateAmountLabel: Label? = null
    var dueDateAmountTextArea: TextArea? = null
    var updateDueDateButton: Button? = null
    var resetDueDateButton: Button? = null
    var cancelDueDateButton: Button? = null
    var dueDateIndex: Int by Delegates.observable(0) {
        prop, old, new ->
        if(new > -1 && new < budgetItem.dueDates.size){
            currentDueDate = budgetItem.dueDates[new]
            dueDateText = currentDueDate.dueDate.toString()
            this.dueDateTextArea!!.text = dueDateText
            dueDateAmountText = currentDueDate.amount.toString()
            this.dueDateAmountTextArea!!.text = dueDateAmountText
        }
    }
    var currentDueDate: DueDate = budgetItem.dueDates[dueDateIndex]


    override fun update(budgetAnalysisState: BudgetAnalysisState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build()
    {
        panel = Components.panel()
                .wrapWithBox(true)
                .wrapWithShadow(false)
                .withSize(Sizes.create(width,height))
                .withTitle("Due Dates")
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
        this.firstButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("<<")
                .withPosition(Positions.create(1,1))
                .build()
        this.firstButton!!.onMouseReleased { this.dueDateIndex = 0 }
        panel!!.addComponent(this.firstButton!!)
        this.previousButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("<")
                .withPosition(Positions.create(1,-1).relativeToRightOf(this.firstButton!!))
                .build()
        this.previousButton!!.onMouseReleased { this.dueDateIndex = Math.max(0,this.dueDateIndex-1) }
        panel!!.addComponent(this.previousButton!!)
        this.nextButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText(">")
                .withPosition(Positions.create(1,-1).relativeToRightOf(this.previousButton!!))
                .build()
        this.nextButton!!.onMouseReleased {
            this.dueDateIndex = Math.min(budgetItem.dueDates.size, this.dueDateIndex+1)
        }
        panel!!.addComponent(this.nextButton!!)
        this.lastButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText(">>")
                .withPosition(Positions.create(1,-1).relativeToRightOf(this.nextButton!!))
                .build()
        this.lastButton!!.onMouseReleased { this.dueDateIndex = budgetItem.dueDates.size-1 }
        panel!!.addComponent(this.lastButton!!)
        dueDateLabel = Components.label()
                .withText("Due Date: ")
                .withPosition(Positions.create(0,-1).relativeToRightOf(this!!.lastButton!!))
                .build()
        panel!!.addComponent(dueDateLabel!!)
        dueDateText = currentDueDate.dueDate.toString()
        dueDateTextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(dueDateText)
                .withSize(Sizes.create(20,1))
                .withPosition(Positions.create(1,-1).relativeToRightOf(dueDateLabel!!))
                .build()
        dueDateTextArea!!.onKeyStroke {
            dueDateText = dueDateTextArea!!.text
        }
        panel!!.addComponent(dueDateTextArea!!)
        dueDateAmountText = currentDueDate.amount.toString()
        dueDateAmountLabel = Components.label()
                .withText("Amount: ")
                .withPosition(Positions.create(0,-1).relativeToRightOf(this!!.dueDateTextArea!!))
                .build()
        panel!!.addComponent(dueDateAmountLabel!!)
        dueDateAmountTextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(dueDateAmountText)
                .withSize(Sizes.create(20,1))
                .withPosition(Positions.create(0,-1).relativeToRightOf(dueDateAmountLabel!!))
                .build()
        dueDateAmountTextArea!!.onKeyStroke {
            dueDateAmountText = dueDateAmountTextArea!!.text
        }
        panel!!.addComponent(dueDateAmountTextArea!!)
        this.updateDueDateButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Update")
                .withPosition(Positions.create(1,-1).relativeToRightOf(dueDateAmountTextArea!!))
                .build()
        this.updateDueDateButton!!.onMouseReleased {
            currentDueDate.dueDate = dateStringParser(dueDateText)
            currentDueDate.amount = dueDateAmountText.toDouble()
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        panel!!.addComponent(this.updateDueDateButton!!)
        this.resetDueDateButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Reset")
                .withPosition(Positions.create(-1,0).relativeToBottomOf(updateDueDateButton!!))
                .build()
        this.resetDueDateButton!!.onMouseReleased {
            dueDateText = currentDueDate.dueDate.toString()
            dueDateAmountText = currentDueDate.amount.toString()
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        panel!!.addComponent(this.resetDueDateButton!!)
        this.cancelDueDateButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Cancel")
                .withPosition(Positions.create(-1,0).relativeToBottomOf(resetDueDateButton!!))
                .build()
        this.cancelDueDateButton!!.onMouseReleased {
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        panel!!.addComponent(this.cancelDueDateButton!!)

    }
}