package view.budget

import model.state.ApplicationState
import model.budget.BudgetItem
import model.enums.Recurrence
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Size
import org.parboiled.common.Tuple2

class YearlyBudgetPanel(var width: Int, var height: Int, var component: Component, var applicationState: ApplicationState) {

    var panel: Panel? = null
    var balanceTuple: Tuple2<Double, Double> = Tuple2(0.0,0.0)
    var previousLabel: Label? = null
    var dividerLabel: Label? = null
    var lowerDividerLabel: Label? = null
    var headerString = "  Occurrence  | Auto | Req |     Due     |         Name         | Xfer |    Amt    |      Monthly      |     Req'd Amt     |      Opt Amt      |    Total Amt"

    fun build(){
        fillOutPanel(null)
    }

    open fun fillOutPanel(additionalHypotheticalBudgetItem: BudgetItem?) {
        panel = Components.panel()
                .wrapWithBox(true)
                .wrapWithShadow(false)
                .withTitle(TITLE)
                .withSize(Size.create(width, height))
                .withPosition(Positions.create(0,1).relativeToBottomOf(component))
                .build()
        val budgetItems = applicationState?.budgetItems
        previousLabel = Components.label()
                .withText(headerString)
                .withPosition(Positions.create(0,1))
                .build()
        panel!!.addComponent(previousLabel!!)
        var dividerStringBuilder: StringBuilder = StringBuilder()
        for(dividerLength in 0..width-6) {
            dividerStringBuilder.append("-")
        }
        dividerLabel = Components.label()
                .withText(dividerStringBuilder.toString())
                .withPosition(Positions.create(0,-1).relativeToBottomOf(previousLabel!!))
                .build()
        panel!!.addComponent(dividerLabel!!)
        budgetItems?.values?.forEach { budgetItem ->
            updateBudgetPanelItems(budgetItem)
        }
        additionalHypotheticalBudgetItem?.let { updateBudgetPanelItems(it) }
        lowerDividerLabel = Components.label()
                .withText(dividerStringBuilder.toString())
                .withPosition(Positions.create(0,-1).relativeToBottomOf(panel!!.children.last()))
                .build()
        panel!!.addComponent(lowerDividerLabel!!)
        panel!!.addComponent(createBalanceLabel())
    }

    fun createBalanceLabel(): Label {
        var balanceStringBuilder = StringBuilder()
        for(balanceStringIndex in balanceStringBuilder.length..(MonthlyBudgetPanel.BUDGET_ITME_LENGTH + MonthlyBudgetPanel.MONTHLY_REQUIRED_AMOUNT_LENGTH -1)){
            balanceStringBuilder.append('\t')
        }
        balanceStringBuilder.append(" | ")
        balanceStringBuilder.append(String.format("%.2f",balanceTuple.a))
        for(balanceStringIndex in balanceStringBuilder.length..(MonthlyBudgetPanel.BUDGET_ITME_LENGTH + 2* MonthlyBudgetPanel.MONTHLY_REQUIRED_AMOUNT_LENGTH -1)){
            balanceStringBuilder.append('\t')
        }
        balanceStringBuilder.append(" | ")
        balanceStringBuilder.append(String.format("%.2f",balanceTuple.b))
        for(balanceStringIndex in balanceStringBuilder.length..(MonthlyBudgetPanel.BUDGET_ITME_LENGTH + 3* MonthlyBudgetPanel.MONTHLY_REQUIRED_AMOUNT_LENGTH -1)){
            balanceStringBuilder.append('\t')
        }
        balanceStringBuilder.append(" | ")
        balanceStringBuilder.append(String.format("%.2f",balanceTuple.a + balanceTuple.b))

        return Components.label()
                .withText(balanceStringBuilder.toString())
                .withPosition(Positions.create(-1,-1).relativeToBottomOf(panel!!.children.last()))
                .build()
    }

    fun updateBudgetPanelItems(budgetItem: BudgetItem) {
        var requiredBalance = balanceTuple.a
        var optionalBalance = balanceTuple.b
        var yearlyAmount = 0.0
        var budgetItemStringBuilder = StringBuilder()
        when(budgetItem.recurrence){
            Recurrence.WEEKLY -> {
                yearlyAmount = 52*budgetItem.actualAmount
                budgetItemStringBuilder.append("     52x      ")
            }
            Recurrence.BIWEEKLY -> {
                yearlyAmount = 26*budgetItem.actualAmount
                budgetItemStringBuilder.append("     26x      ")
            }
            Recurrence.MONTHLY -> {
                yearlyAmount = 12*budgetItem.actualAmount
                budgetItemStringBuilder.append("     12x      ")
            }
            Recurrence.YEARLY, Recurrence.ONETIME -> {
                yearlyAmount = budgetItem.actualAmount
                budgetItemStringBuilder.append("     1x       ")
            }
        }
        if(budgetItem.required) {
            requiredBalance = requiredBalance + yearlyAmount
        } else {
            optionalBalance = optionalBalance + yearlyAmount
        }
        if(budgetItemStringBuilder.length > 0){
            budgetItemStringBuilder.append(budgetItem.toNarrowString())
            for(budgetTextLineIndex in budgetItemStringBuilder.length..BUDGET_ITME_LENGTH){
                budgetItemStringBuilder.append("\t")
            }
            budgetItemStringBuilder.append("  | ")
            budgetItemStringBuilder.append(String.format("%.2f",yearlyAmount))
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
        val TITLE = "Yearly Budget"
        val BUDGET_ITME_LENGTH = 80
        val MONTHLY_REQUIRED_AMOUNT_LENGTH = 20

    }
}