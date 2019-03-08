package control.handlers

import control.handlers.core.BaseHandler
import model.handler.HandlerType

class HandlerRegistry {

    companion object {
        var handlers: HashMap<HandlerType, HashSet<BaseHandler>> = hashMapOf(
                HandlerType.ItemConfig to hashSetOf(),
                HandlerType.TaskConfig to hashSetOf(),
                HandlerType.Update to hashSetOf(),
                HandlerType.Budget to hashSetOf(),
                HandlerType.Calendar to hashSetOf(),
                HandlerType.Task to hashSetOf()
        )

        fun register(baseHandler: BaseHandler, handlerType: HandlerType){
            handlers.get(handlerType)?.add(baseHandler)
        }

        fun deregister(baseHandler: BaseHandler, handlerType: HandlerType){
            handlers.get(handlerType)?.remove(baseHandler)
        }

        fun clearHandlerType(handlerType: HandlerType){
            handlers.get(handlerType)?.clear()
        }

        fun clearAllHandlerTypes(){
            HandlerType.values().forEach { handlerType -> clearHandlerType(handlerType) }
        }

        fun getAllHandlersByType(handlerType: HandlerType): HashSet<BaseHandler>?{
            return handlers.get(handlerType)
        }

        fun updateByHandlerType(handlerType: HandlerType, varargs: Any) {
            when(handlerType){
                HandlerType.ItemConfig -> handleItemConfigUpdate(varargs)
                HandlerType.TaskConfig -> handleTaskConfigUpdate(varargs)
                HandlerType.Update -> handleUpdate(varargs)
                HandlerType.Budget -> handleBudgetUpdate(varargs)
                HandlerType.Calendar -> handleCalendarUpdate(varargs)
                HandlerType.Task -> handleTaskConfigUpdate(varargs)
            }
        }

        private fun handleCalendarUpdate(varargs: Any) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        private fun handleBudgetUpdate(varargs: Any) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        private fun handleUpdate(varargs: Any) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        private fun handleTaskConfigUpdate(varargs: Any) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        private fun handleItemConfigUpdate(varargs: Any) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

}