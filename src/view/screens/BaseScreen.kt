package view.screens

import model.ApplicationState
import model.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Screens
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.grid.TileGrid
import view.input.InputPanel

abstract class BasePanel(var width: Int, var height: Int, var tileGrid: TileGrid,
                                var applicationUIComponents: ApplicationUIComponents) {

    var inputPanel: InputPanel? = null

    abstract fun build()

    abstract fun projectBalances()

    abstract fun currentBalances()

    abstract fun prevBudgetState()

    abstract fun nextBudgetState()

    open fun clear() {
    }

    abstract fun update(): BudgetState

    open fun display() {
    }

    abstract fun clearInputPanel()
    abstract fun updateInputPanel(panel: Panel)
}