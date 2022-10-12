sealed class Result<out L, out R> {
    data class Failure<out L>(val value: L) : Result<L, Nothing>()
    data class Success<out R>(val value: R) : Result<Nothing, R>()
}