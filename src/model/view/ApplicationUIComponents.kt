package model.view

import control.ApplicationStateBudgetAnalysis
import model.ApplicationState
import model.BudgetState
import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.screen.Screen
import view.accounts.AccountsPanel
import view.budgetState.BudgetStatePanel
import view.input.InputPanel
import view.items.ItemsPanel
import java.time.LocalDateTime

class ApplicationUIComponents() {

    var budgetStatePanel: BudgetStatePanel? = null
    var accountsPanel: AccountsPanel? = null
    var itemsPanel: ItemsPanel? = null
    var inputPanel: InputPanel? = null
    var screen: Screen? = null
    var applicationState: ApplicationState? = null

    fun build(){

        val fullScreenSize: Size = Size.create(160,98)
        val tileGrid = SwingApplications.startTileGrid(
                AppConfigs.newConfig()
                        .withSize(fullScreenSize)
                        .withDefaultTileset(CP437TilesetResources.rexPaint12x12())
                        .build())
        screen = Screens.createScreenFor(tileGrid)

        val fifthScreenWidth: Int = fullScreenSize.width/5
        val sixthScreenHeight: Int = fullScreenSize.height/6+2

        budgetStatePanel = BudgetStatePanel(fifthScreenWidth * 3+1,
                sixthScreenHeight * 2, this!!.applicationState!!)
        budgetStatePanel!!.startDate = LocalDateTime.now()
        budgetStatePanel!!.endDate = budgetStatePanel!!.startDate.plusDays(6).plusHours(23).plusMinutes(59)
        budgetStatePanel!!.build()

        accountsPanel = AccountsPanel(fifthScreenWidth*2 - 1, sixthScreenHeight * 2,
                                budgetStatePanel!!.panel!!, applicationState!!)
        accountsPanel!!.build()

        itemsPanel = ItemsPanel(fullScreenSize.width, sixthScreenHeight*3-2,
                                budgetStatePanel!!.panel!!, applicationState!!)
        itemsPanel!!.build()

        inputPanel = InputPanel(fullScreenSize.width, sixthScreenHeight-10, itemsPanel!!.panel!!, applicationState!!)
        inputPanel!!.build()

        screen!!.addComponent(budgetStatePanel!!.panel!!)
        screen!!.addComponent(accountsPanel!!.panel!!)
        screen!!.addComponent(itemsPanel!!.panel!!)
        screen!!.addComponent(inputPanel!!.panel!!)

        // we can apply color themes to a screen
        screen!!.applyColorTheme(ColorThemes.monokaiBlue())
        // in order to see the changes you need to display your screen.
        screen!!.display()

    }

    fun clear() {
        screen?.clear()
    }

    fun display() {
        screen?.display()
    }

    fun update(): BudgetState {
        var applicationStateBudgetAnalysis = ApplicationStateBudgetAnalysis(applicationState!!)
        var budgetState = applicationState!!.currentPayPeriodBudgetState
        var currentBudgetAnalysisStates = applicationStateBudgetAnalysis.performBudgetAnalysis(false)
        budgetStatePanel?.update()
        accountsPanel?.update(currentBudgetAnalysisStates.first()!!)
        itemsPanel?.update()
        return budgetState!!
    }
}