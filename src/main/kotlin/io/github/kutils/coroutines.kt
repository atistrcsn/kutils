package io.github.kutils

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * @author atistrcsn - 2017
 */

fun <T> future(context: CoroutineContext = ForkJoinPool.commonPool().asCoroutineDispatcher(), block: suspend () -> T): CompletableFuture<T> =
        CompletableFutureCoroutine<T>(context)
                .also {
                    block.startCoroutine(completion = it)
                }

class CompletableFutureCoroutine<T>(override val context: CoroutineContext) : CompletableFuture<T>(), Continuation<T> {
    override fun resumeWith(result: Result<T>) {
        if (result.isSuccess)
            result.getOrNull()?.let {
                complete(it)
            }
        else
            result.exceptionOrNull()?.let {
                completeExceptionally(it)
            }
    }
}

suspend fun <T> CompletableFuture<T>.await(): T =
        suspendCoroutine { cont: Continuation<T> ->
            whenComplete { result, exception ->
                if (exception == null) // the future has been completed normally
                    cont.resume(result)
                else // the future has completed with an exception
                    cont.resumeWithException(exception)
            }
        }

