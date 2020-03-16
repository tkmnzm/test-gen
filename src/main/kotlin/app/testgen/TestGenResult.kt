package app.testgen

sealed class TestGenResult<T>(val isSuccess: Boolean) {

    data class Success<T>(val data: T) : TestGenResult<T>(true)

    data class Failure<T>(val throwable: Throwable) : TestGenResult<T>(false)

    companion object {
        fun <T> success(data: T): Success<T> =
            Success(data)
        fun <T> failure(error: Throwable): Failure<T> =
            Failure(error)
    }
}


