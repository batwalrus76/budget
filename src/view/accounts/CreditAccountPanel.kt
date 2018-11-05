package view.accounts

import model.ApplicationState
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.items.BaseItemsPanel

class CreditAccountPanel(width: Int,  height: Int, component: Component, applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, applicationState) {

    override fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(CREDIT_ACCOUNTS_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(AccountsPanel.DEFAULT_OFFSET.relativeToBottomOf(component))
                .build()
        super.build()
        applicationState.creditAccounts!!.forEach { account ->
            radioButtonGroup!!.addOption(account.name, account.toString())}
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    override fun update() {
        super.update()
        applicationState.creditAccounts!!.forEach { account ->
            radioButtonGroup!!.addOption(account.name, account.toString())}
    }


    companion object {
        val CREDIT_ACCOUNTS_TITLE: String = "Credit Accounts"
    }
}