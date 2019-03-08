package view.configuration.task

import control.handlers.configuration.TaskConfigurationHandler
import model.representation.state.ApplicationState
import model.tasks.Priority
import model.tasks.Task
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.builder.component.LabelBuilder
import org.hexworks.zircon.api.builder.component.RadioButtonGroupBuilder
import org.hexworks.zircon.api.builder.component.TextAreaBuilder
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.component.TextArea
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.kotlin.onSelection
import view.abstracts.BaseConfigurationPanel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min

class TasksConfigurationPanel (var id:Int? = null, var project:String = " ", var due: LocalDate? = null,
                               var description: String, var priority: Priority,
                               width: Int, height: Int,
                               var applicationState: ApplicationState,
                               useHorizontalLayout: Boolean = false,
                               position: Position = Positions.create(0,0),
                               wrapWithBox: Boolean = false):
        BaseConfigurationPanel(width, height, useHorizontalLayout, position, wrapWithBox), TaskConfigurationHandler{

    var projectLabel: Label? = null
    var projectTextArea: TextArea? = null
    var dueLabel: Label? = null
    var dueTextArea: TextArea? = null
    var descriptionLabel: Label? = null
    var descriptionTextArea: TextArea? = null
    var priorityLabel: Label? = null
    var priorityButtonGroup: RadioButtonGroup? = null
    var titleLabel: Label? = null


    override fun processPanelComponents() {
        titleLabel = Components.label()
                .withText("Configure Task")
                .withPosition(Positions.create(0,0))
                .build()

        val projectLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Project:")
        val projectTextAreaBuilder = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(20,2))
        val dueLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Due: ")
        var dueString = ""
        if(due != null){
            dueString = due!!.format(DateTimeFormatter.ISO_DATE)
        }
        val dueTextAreaBuilder = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(dueString)
                .withSize(Sizes.create(10,2))
        val descriptionLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Description:")
        val descriptionTextAreaBuilder = Components.textArea()
                .wrapWithBox(true)
                .withSize(Sizes.create(40,4))
                .wrapWithShadow(false)
        if(description != null && description.length > 0){
            descriptionTextAreaBuilder.withText(description)
        }
        val priorityLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Priority: ")
        val priorityButtonGroupBuilder = Components.radioButtonGroup()
                .withSize(Sizes.create(20, 4))

        when(useHorizontalLayout) {
            true -> createHorizontalLayoutPanel(projectLabelBuilder, projectTextAreaBuilder, dueLabelBuilder,
                    dueTextAreaBuilder, descriptionLabelBuilder, descriptionTextAreaBuilder,
                    priorityLabelBuilder, priorityButtonGroupBuilder)
            false -> createVerticalLayoutPanel(projectLabelBuilder, projectTextAreaBuilder, dueLabelBuilder,
                    dueTextAreaBuilder, descriptionLabelBuilder, descriptionTextAreaBuilder,
                    priorityLabelBuilder, priorityButtonGroupBuilder)
        }
    }

    private fun createHorizontalLayoutPanel(projectLabelBuilder: LabelBuilder, projectTextAreaBuilder: TextAreaBuilder,
                                            dueLabelBuilder: LabelBuilder, dueTextAreaBuilder: TextAreaBuilder,
                                            descriptionLabelBuilder: LabelBuilder,
                                            descriptionTextAreaBuilder: TextAreaBuilder,
                                            priorityLabelBuilder: LabelBuilder,
                                            priorityButtonGroupBuilder: RadioButtonGroupBuilder) {
        var projectTextString = " "
        if(project.length > 1) {
            projectTextString = project.substring(0, min(project.length - 1, width - 4))
        }
        projectLabel = projectLabelBuilder
                .withPosition(Positions.create(0,1).relativeToBottomOf(this!!.titleLabel!!))
                .withText("Project: ")
                .build()
        projectTextArea = projectTextAreaBuilder.withPosition(Positions.create(1,0)
                .relativeToRightOf(projectLabel!!))
                .withText(projectTextString)
                .build()
        dueLabel = dueLabelBuilder.withPosition(Positions.create(0,0).relativeToRightOf(projectTextArea!!))
                .build()
        dueTextArea = dueTextAreaBuilder.withPosition(Positions.create(0,0).relativeToRightOf(dueLabel!!))
                .build()
        priorityLabel = priorityLabelBuilder
                .withPosition(Positions.create(0,0).relativeToRightOf(dueTextArea!!))
                .build()
        priorityButtonGroup = priorityButtonGroupBuilder
                .withPosition(Position.create(0,0).relativeToRightOf(priorityLabel!!))
                .build()
        descriptionLabel = descriptionLabelBuilder
                .withPosition(Positions.create(0,0).relativeToRightOf(priorityButtonGroup!!))
                .build()
        descriptionTextArea = descriptionTextAreaBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(descriptionLabel!!))
                .build()
    }

    private fun createVerticalLayoutPanel(projectLabelBuilder: LabelBuilder, projectTextAreaBuilder: TextAreaBuilder,
                                            dueLabelBuilder: LabelBuilder, dueTextAreaBuilder: TextAreaBuilder,
                                            descriptionLabelBuilder: LabelBuilder,
                                          descriptionTextAreaBuilder: TextAreaBuilder,
                                            priorityLabelBuilder: LabelBuilder,
                                            priorityButtonGroupBuilder: RadioButtonGroupBuilder) {
        projectLabel = projectLabelBuilder
                .withPosition(Positions.create(0,1).relativeToBottomOf(this!!.titleLabel!!))
                .build()
        projectTextArea = projectTextAreaBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(projectLabel!!))
                .withText(project.substring(0, min(project.length,20)))
                .build()
        dueLabel = dueLabelBuilder.withPosition(Positions.create(0,1).relativeToBottomOf(projectLabel!!))
                .build()
        dueTextArea = dueTextAreaBuilder.withPosition(Positions.create(1,0).relativeToRightOf(dueLabel!!))
                .build()
        descriptionLabel = descriptionLabelBuilder.withPosition(Positions.create(0,2)
                .relativeToBottomOf(dueLabel!!))
                .build()
        descriptionTextArea = descriptionTextAreaBuilder
                .withPosition(Position.create(1,0).relativeToRightOf(descriptionLabel!!))
                .build()
        priorityLabel = priorityLabelBuilder
                .withPosition(Positions.create(0,5).relativeToBottomOf(descriptionLabel!!))
                .build()
        priorityButtonGroup = priorityButtonGroupBuilder
                .withPosition(Position.create(0,0).relativeToRightOf(priorityLabel!!))
                .build()
    }

    override fun updatePanelWithComponents() {
        panel?.children?.forEach { panel?.removeComponent(it) }
        titleLabel?.let { panel!!.addComponent(it) }
        projectLabel?.let { panel!!.addComponent(it) }
        projectTextArea?.let { panel!!.addComponent(it) }
        dueLabel?.let { panel!!.addComponent(it) }
        dueTextArea?.let { panel!!.addComponent(it) }
        descriptionLabel?.let { panel!!.addComponent(it) }
        descriptionTextArea?.let { panel!!.addComponent(it) }
        priorityLabel?.let { panel!!.addComponent(it) }
        priorityButtonGroup?.let { panel!!.addComponent(it) }
    }

    override fun addComponentBehaviours() {
        when(priority){
            Priority.L -> {
                priorityButtonGroup?.addOption("L", "L (current)")
                priorityButtonGroup?.addOption("M", "M")
                priorityButtonGroup?.addOption("H", "H")
            }
            Priority.M -> {
                priorityButtonGroup?.addOption("L", "L")
                priorityButtonGroup?.addOption("M", "M (current)")
                priorityButtonGroup?.addOption("H", "H")
            }
            Priority.H -> {
                priorityButtonGroup?.addOption("L", "L")
                priorityButtonGroup?.addOption("M", "M")
                priorityButtonGroup?.addOption("H", "H (current)")
            }
        }
        priorityButtonGroup?.onSelection {
            it ->
            priority = Priority.valueOf(it.value)
        }
    }

    open fun generateTask(): Task {
        var dueDate:LocalDate? = null
        if(dueTextArea!!.text.length > 0){
            dueDate = LocalDate.parse(dueTextArea!!.text)
        }
        return Task(id, null, projectTextArea!!.text, dueDate,
                descriptionTextArea!!.text, null, priority)
    }

    override fun handleConfigurationTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleClear() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}