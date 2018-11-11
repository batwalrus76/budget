package view.screens

import model.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.Screens
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.grid.TileGrid
import view.budget.MonthlyBudgetPanel
import view.control.MainControlsPanel

class BudgetScreen(width: Int, height: Int, tileGrid: TileGrid, var uiComponents: ApplicationUIComponents) :
        BaseScreen(width, height, tileGrid, uiComponents) {

    var mainControlPanel: MainControlsPanel? = null
    var monthlyBudgetPanel: MonthlyBudgetPanel? = null

    override fun update(): BudgetState {
        return uiComponents.currentViewedBudgetState!!
    }

    override fun clearInputPanel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateInputPanel(panel: Panel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun projectBalances() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun currentBalances() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun prevBudgetState() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun nextBudgetState() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun build() {
        screen = Screens.createScreenFor(tileGrid)
        mainControlPanel = MainControlsPanel(width-2, 6, uiComponents)
        mainControlPanel!!.build()
        monthlyBudgetPanel = MonthlyBudgetPanel(width-2, height- mainControlPanel!!.height-2,
                mainControlPanel!!.panel!!, uiComponents.applicationState!!)
        monthlyBudgetPanel!!.build()
        mainControlPanel!!.panel?.let { screen.addComponent(it) }
        monthlyBudgetPanel!!.panel?.let { screen.addComponent(it) }
        screen!!.applyColorTheme(ColorThemes.monokaiBlue())
    }

}