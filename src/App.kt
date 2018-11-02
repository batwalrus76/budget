import control.ApplicationStateCLIProcessor
import control.ApplicationStateManager
import model.ApplicationState
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val stateFileLocation: String = "/Users/pascact1/.budget"
    val stateFile = File(stateFileLocation)
    var applicationState: ApplicationState?

    if (stateFile?.exists()) {
        applicationState = ApplicationState.deserializeJsonToApplicationState(stateFile)
    } else {
        applicationState = ApplicationState()
    }
    var applicationStateManager: ApplicationStateManager = ApplicationStateManager(applicationState)
    applicationStateManager.reconcilePastCurrentFutureBudgetStates()

    var applicationStateCLIProcessor: ApplicationStateCLIProcessor =
            ApplicationStateCLIProcessor(applicationState, applicationStateManager)

    var continueRunning = true

    while (continueRunning) {
        //Options add/remove budget items, add/remove savings accounts, add/remove credit accounts
        continueRunning = applicationStateCLIProcessor.cliEntryPoint(stateFile)
    }
    exitProcess(0)
}

