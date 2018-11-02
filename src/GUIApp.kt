import org.hexworks.zircon.api.*
import view.AccountsPanel
import view.BudgetStatePanel
import view.CommandsPanel
import view.ItemsPanel
import java.time.LocalDateTime


object GUIApp {

    @JvmStatic
    fun main(args: Array<String>) {

        val tileGrid = SwingApplications.startTileGrid(
                AppConfigs.newConfig()
                        .withSize(Sizes.create(160, 98))
                        .withDefaultTileset(CP437TilesetResources.rexPaint12x12())
                        .build())
        val screen = Screens.createScreenFor(tileGrid)

        var budgetStatePanel = BudgetStatePanel()
        budgetStatePanel.startDate = LocalDateTime.now()
        budgetStatePanel.endDate = budgetStatePanel.startDate.plusDays(6).plusHours(23).plusMinutes(59)
        budgetStatePanel.build()

        var accountsPanel = AccountsPanel()
        accountsPanel.build()

        var itemsPanel = ItemsPanel()
        itemsPanel.build()

        var commandsPanel = CommandsPanel()
        commandsPanel.build()

        screen.addComponent(budgetStatePanel.panel!!)
        screen.addComponent(accountsPanel.panel!!)
        screen.addComponent(itemsPanel.panel!!)
        screen.addComponent(commandsPanel.panel!!)

        // we can apply color themes to a screen
        screen.applyColorTheme(ColorThemes.monokaiBlue())

        // this is how you can define interactions with a component
//        left.onMouseReleased({ mouseAction -> screen.applyColorTheme(ColorThemes.monokaiGreen()) })
//
//        right.onMouseReleased({ mouseAction -> screen.applyColorTheme(ColorThemes.monokaiViolet()) })

        // in order to see the changes you need to display your screen.
        screen.display()
    }
}