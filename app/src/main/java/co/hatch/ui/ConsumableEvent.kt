package co.hatch.ui

import java.util.concurrent.atomic.AtomicBoolean

class ConsumableEvent<T>(
    private val value: T,
) {

    private val isConsumed = AtomicBoolean(false)

    fun consume(): T? {
        val isSuccess = isConsumed.compareAndSet(false, true)
        return if (isSuccess) {
            value
        } else {
            null
        }
    }
}
