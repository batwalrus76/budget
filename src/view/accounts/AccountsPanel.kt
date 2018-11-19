package view.accounts

import model.ApplicationState
import model.BudgetAnalysisState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.data.Position
import utils.DateTimeUtils
import view.items.BaseItemsPanel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AccountsPanel(width: Int,  height: Int, component: Component, uiComponents: ApplicationUIComponents, applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, uiComponents, applicationState) {

    var date: LocalDateTime = DateTimeUtils.currentTime()
    var dateLabel: Label = Components.label()
            .withText("Date: ${date.format(DateTimeFormatter.ISO_DATE)}")
            .withPosition(Position.offset1x1())
            .build()
    var totalSubPanelHeight:Int? = null
    var individualSubPanelHeight: Int? = null
    var multiAccountSubPanelHeight: Int? = null
    var checkingAccountPanel: CheckingAccountPanel? = null
    var savingsAccountsPanel: SavingsAccountPanel? = null
    var creditAccountsPanel: CreditAccountPanel? = null

    override fun build(){
        totalSubPanelHeight = height - this.dateLabel.height
        individualSubPanelHeight = (totalSubPanelHeight!! /7)-1
        multiAccountSubPanelHeight = 3*individualSubPanelHeight!!
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width!!, this.height!!)) // the size must be smaller than the parent's size
                .withPosition(ZERO_OFFSET.relativeToRightOf(component!!))
                .build() // position is always relative to the parent
        this.checkingAccountPanel = CheckingAccountPanel(width!!-4, individualSubPanelHeight!!+1,
                                dateLabel, uiComponents, applicationState)
        this.checkingAccountPanel!!.build()
        this.savingsAccountsPanel =
                SavingsAccountPanel(width!!-4, multiAccountSubPanelHeight!!+1,
                        checkingAccountPanel!!.panel!!, uiComponents, applicationState!!)
        this.savingsAccountsPanel!!.build()
        this.checkingAccountPanel!!.build()
        this.creditAccountsPanel = CreditAccountPanel(width!!-4, multiAccountSubPanelHeight!!+1,
                        savingsAccountsPanel!!.panel!!, uiComponents, applicationState)
        this.creditAccountsPanel!!.build()
        this.panel!!.addComponent(dateLabel)
        this.panel!!.addComponent(checkingAccountPanel!!.panel!!)
        this.panel!!.addComponent(savingsAccountsPanel!!.panel!!)
        this.panel!!.addComponent(creditAccountsPanel!!.panel!!)
    }

    fun update(budgetAnalysisState: BudgetAnalysisState){
        checkingAccountPanel!!.update(budgetAnalysisState)
        savingsAccountsPanel!!.update(budgetAnalysisState)
        creditAccountsPanel!!.update(budgetAnalysisState)
    }

    override fun update(){
        checkingAccountPanel!!.update(applicationState!!.checkingAccount!!.balance)
        savingsAccountsPanel!!.update()
        creditAccountsPanel!!.update()
    }

    companion object {
        val TITLE: String = "Accounts"
        val ZERO_OFFSET = Positions.create(0,0)
        val DEFAULT_OFFSET = Positions.create(0,1)
    }
}