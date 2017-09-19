package lt.saltyjuice.dragas.chatty.v3.core.route

import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller

private val controllerMap: HashMap<Class<out Controller>, Controller> = HashMap()

fun getController(clazz: Class<out Controller>): Controller
{
    val controller = controllerMap[clazz] ?: clazz.newInstance()
    controllerMap[clazz] = controller
    return controller
}

fun setController(controller: Controller)
{
    controllerMap[controller::class.java] = controller
}