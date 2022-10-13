sealed class ActionResult<out L, out R> {
    data class Failure<out L>(val value: L) : ActionResult<L, Nothing>()
    data class Success<out R>(val value: R) : ActionResult<Nothing, R>()
}