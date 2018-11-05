import control.ApplicationStateCLIProcessor
import control.ApplicationStateManager
import control.ApplicationStateManager.Companion.DEFAULT_STATE_FILE
import control.ApplicationStateManager.Companion.DEFAULT_STATE_FILE_LOCATION
import control.ApplicationStateManager.Companion.buildApplicationStateFromDefaultFileLocation
import model.ApplicationState
import model.BudgetState
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    var applicationState: ApplicationState = buildApplicationStateFromDefaultFileLocation()
    
    var applicationStateManager: ApplicationStateManager = ApplicationStateManager(applicationState)
    applicationStateManager.reconcilePastCurrentFutureBudgetStates()

    var applicationStateCLIProcessor: ApplicationStateCLIProcessor =
            ApplicationStateCLIProcessor(applicationState, applicationStateManager)


    var workingBudgetState: BudgetState? = BudgetState()

    while (workingBudgetState != null) {
        //Options add/remove budget items, add/remove savings accounts, add/remove credit accounts
        workingBudgetState = applicationStateCLIProcessor.cliEntryPoint(DEFAULT_STATE_FILE)
    }
    exitProcess(0)
}

