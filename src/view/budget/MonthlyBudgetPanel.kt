package view.budget

import model.state.ApplicationState
import model.budget.BudgetItem
import model.enums.Recurrence
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Size
import org.parboiled.common.Tuple2

class MonthlyBudgetPanel(var width: Int, var height: Int, var applicationState: ApplicationState) {

    var panel: Panel? = null
    var balanceTuple: Tuple2<Double, Double> = Tuple2(0.0,0.0)
    var previousLabel: Label? = null
    var dividerLabel: Label? = null
    var lowerDividerLabel: Label? = null
    var headerString = "  Occurrence | Auto | Req |     Due     |         Name         | Xfer |    Amt    |      Monthly      |     Req'd Amt     |      Opt Amt      |    Total Amt"
    var dividerString = "-------------------------------------------------------------------------------------------------------------------------------------------------------------"

    fun build(){
        fillOutPanel(null)
    }

    open fun fillOutPanel(additionalBudgetItem: BudgetItem?) {
        panel = Components.panel()
                .wrapWithBox(true)
                .wrapWithShadow(false)
                .withTitle(TITLE)
                .withSize(Size.create(width, height))
                .withPosition(Positions.create(0,0))
                .build()
        var budgetItems = applicationState?.budgetItems
        previousLabel = Components.label()
                .withText(headerString)
                .withPosition(Positions.create(0,1))
                .build()
        panel!!.addComponent(this.previousLabel!!)
        dividerLabel = Components.label()
                .withText(dividerString)
                .withPosition(Positions.create(0,-1).relativeToBottomOf(previousLabel!!))
                .build()
        panel!!.addComponent(dividerLabel!!)
        budgetItems?.values?.forEach { budgetItem ->
            when(budgetItem.recurrence){
                Recurrence.DAILY, Recurrence.WEEKLY, Recurrence.BIWEEKLY, Recurrence.MONTHLY ->
                    updateBudgetPanelItems(budgetItem)
            }
        }
        additionalBudgetItem?.let { updateBudgetPanelItems(it) }
        lowerDividerLabel = Components.label()
                .withText(dividerString)
                .withPosition(Positions.create(0,-1).relativeToBottomOf(panel!!.children.last()))
                .build()
        panel!!.addComponent(lowerDividerLabel!!)
        panel!!.addComponent(createBalanceLabel())
    }

    fun createBalanceLabel(): Label {
        var balanceStringBuilder = StringBuilder()
        for(balanceStringIndex in balanceStringBuilder.length..(BUDGET_ITME_LENGTH+ MONTHLY_REQUIRED_AMOUNT_LENGTH-1)){
            balanceStringBuilder.append('\t')
        }
        balanceStringBuilder.append(" | ")
        balanceStringBuilder.append(String.format("%.2f",balanceTuple.a))
        for(balanceStringIndex in balanceStringBuilder.length..(BUDGET_ITME_LENGTH+ 2*MONTHLY_REQUIRED_AMOUNT_LENGTH-1)){
            balanceStringBuilder.append('\t')
        }
        balanceStringBuilder.append(" | ")
        balanceStringBuilder.append(String.format("%.2f",balanceTuple.b))
        for(balanceStringIndex in balanceStringBuilder.length..(BUDGET_ITME_LENGTH+ 3*MONTHLY_REQUIRED_AMOUNT_LENGTH-1)){
            balanceStringBuilder.append('\t')
        }
        balanceStringBuilder.append(" | ")
        balanceStringBuilder.append(String.format("%.2f",balanceTuple.a + balanceTuple.b))

        return Components.label()
                .withText(balanceStringBuilder.toString())
                .withPosition(Positions.create(-1,-1).relativeToBottomOf(panel!!.children.last()))
                .build()
    }

    open fun updateBudgetPanelItems(budgetItem: BudgetItem){
        var requiredBalance = balanceTuple.a
        var optionalBalance = balanceTuple.b
        var monthlyAmount = 0.0
        var budgetItemStringBuilder = StringBuilder()
        when(budgetItem.recurrence){
            Recurrence.WEEKLY -> {
                monthlyAmount = 4*budgetItem.actualAmount
                budgetItemStringBuilder.append("     4x      ")
            }
            Recurrence.BIWEEKLY -> {
                monthlyAmount = 2*budgetItem.actualAmount
                budgetItemStringBuilder.append("     2x      ")
            }
            Recurrence.MONTHLY -> {
                monthlyAmount = budgetItem.actualAmount
                budgetItemStringBuilder.append("     1x      ")
            }
        }
        if(budgetItem.required) {
            requiredBalance = requiredBalance + monthlyAmount
        } else {
            optionalBalance = optionalBalance + monthlyAmount
        }
        if(budgetItemStringBuilder.length > 0){
            budgetItemStringBuilder.append(budgetItem.toNarrowString())
            for(budgetTextLineIndex in budgetItemStringBuilder.length..BUDGET_ITME_LENGTH){
                budgetItemStringBuilder.append("\t")
            }
            budgetItemStringBuilder.append(" | ")
            budgetItemStringBuilder.append(monthlyAmount)
            for(budgetTextLineIndex in
                            budgetItemStringBuilder.length..(BUDGET_ITME_LENGTH+ MONTHLY_REQUIRED_AMOUNT_LENGTH)){
                budgetItemStringBuilder.append("\t")
            }
            budgetItemStringBuilder.append(" | ")
            budgetItemStringBuilder.append(String.format("%.2f",requiredBalance))
            for(budgetTextLineIndex in
                budgetItemStringBuilder.length..(BUDGET_ITME_LENGTH+2*MONTHLY_REQUIRED_AMOUNT_LENGTH)){
                budgetItemStringBuilder.append("\t")
            }
            budgetItemStringBuilder.append(" | ")
            budgetItemStringBuilder.append(String.format("%.2f",optionalBalance))
            for(budgetTextLineIndex in
                budgetItemStringBuilder.length..(BUDGET_ITME_LENGTH+3*MONTHLY_REQUIRED_AMOUNT_LENGTH)){
                budgetItemStringBuilder.append("\t")
            }
            budgetItemStringBuilder.append(" | ")
            budgetItemStringBuilder.append(String.format("%.2f",requiredBalance+optionalBalance))
            if(!panel!!.children.isEmpty()){
                val label = Components.label()
                        .withText(budgetItemStringBuilder.toString())
                        .withPosition(Positions.create(-1,-1).relativeToBottomOf(panel!!.children.last()))
                        .build()
                panel!!.addComponent(label!!)
            } else {
                val label = Components.label()
                        .withText(budgetItemStringBuilder.toString())
                        .withPosition(Positions.create(1,0))
                        .build()
                panel!!.addComponent(label!!)
            }
        }
        balanceTuple = Tuple2(requiredBalance, optionalBalance)
    }

    companion object {
        val TITLE = "Monthly Budget"
        val BUDGET_ITME_LENGTH = 80
        val MONTHLY_REQUIRED_AMOUNT_LENGTH = 20

    }
}