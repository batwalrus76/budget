import control.ApplicationStateManager
import model.ApplicationState
import model.BudgetState
import model.view.ApplicationUIComponents
import kotlin.system.exitProcess


object GUIApp {

    @JvmStatic
    fun main(args: Array<String>) {
        var applicationUIComponents = ApplicationUIComponents()
        var applicationState: ApplicationState = ApplicationStateManager.buildApplicationStateFromDefaultFileLocation()
        applicationUIComponents.applicationState = applicationState
        applicationUIComponents.build()
        var applicationStateManager = ApplicationStateManager(applicationState)
        applicationStateManager.reconcilePastCurrentFutureBudgetStates()

        var workingBudgetState: BudgetState? = applicationState.currentPayPeriodBudgetState
        while (workingBudgetState != null) {
            applicationUIComponents.clear()
            workingBudgetState = applicationUIComponents.update()
            applicationUIComponents.display()
            Thread.sleep(60_000)
        }
        exitProcess(0)
    }
}