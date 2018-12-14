import control.ApplicationStateBudgetAnalysis
import control.ApplicationStateManager
import model.state.ApplicationState
import model.budget.BudgetState
import model.view.ApplicationUIComponents
import kotlin.system.exitProcess


object GUIApp {

    @JvmStatic
    fun main(args: Array<String>) {

        var applicationState: ApplicationState = ApplicationStateManager.buildApplicationStateFromDefaultFileLocation()!!
        var applicationStateBudgetAnalysis = ApplicationStateBudgetAnalysis(applicationState)
        var applicationStateManager = ApplicationStateManager(applicationState,applicationStateBudgetAnalysis)
        applicationStateManager.reconcileApplicationStateToTodaysDate()
        var applicationUIComponents = ApplicationUIComponents(applicationStateBudgetAnalysis, applicationState)
        applicationUIComponents.build()
        var workingBudgetState: BudgetState? = applicationState.currentPayPeriodBudgetState
        applicationUIComponents.weeklyOverviewScreen?.currentBalances()
        while (workingBudgetState != null) {
            applicationUIComponents.clear()
            workingBudgetState = applicationUIComponents.update()
            applicationUIComponents.display()
            Thread.sleep(120_000)
        }
        exitProcess(0)
    }
}