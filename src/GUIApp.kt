import org.hexworks.zircon.api.*
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
        accountsPanel.date = LocalDateTime.now()
        accountsPanel.build()

        val itemsPanel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle("Items") // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(160, 24)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,50))
                .build() // position is always relative to the parent

        val commandsPanel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle("Commands") // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(160, 24)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,74))
                .build() // position is always relative to the parent

        screen.addComponent(budgetStatePanel.panel!!)
        screen.addComponent(accountsPanel.panel!!)
        screen.addComponent(itemsPanel)
        screen.addComponent(commandsPanel)

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