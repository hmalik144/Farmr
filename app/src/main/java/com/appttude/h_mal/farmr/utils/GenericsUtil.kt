package com.appttude.h_mal.farmr.utils

import com.appttude.h_mal.farmr.model.Order
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
fun <CLASS : Any> Any.getGenericClassAt(position: Int): KClass<CLASS> =
    ((javaClass.genericSuperclass as? ParameterizedType)
        ?.actualTypeArguments?.getOrNull(position) as? Class<CLASS>)
        ?.kotlin
        ?: throw IllegalStateException("Can not find class from generic argument")

/**
 * @param validate when result is false then we trigger
 * @param onError
 *
 *
 * @sample
 * var s: String?
 * i.validate{!i.isNullOrEmpty()} { print("string is empty") }
 */
inline fun <T : Any?> T.validateField(validate: (T) -> Boolean, onError: () -> Unit) {
    if (!validate.invoke(this)) {
        onError.invoke()
    }
}

/**
 * Returns a list of all elements sorted according to the specified comparator. In order of ascending or descending
 * The sort is stable. It means that equal elements preserve their order relative to each other after sorting.
 */
inline fun <T, R : Comparable<R>> Iterable<T>.sortedByOrder(
    order: Order = Order.ASCENDING,
    crossinline selector: (T) -> R?
): List<T> {
    return when (order) {
        Order.ASCENDING -> sortedWith(compareBy(selector))
        Order.DESCENDING -> sortedWith(compareByDescending(selector))
    }
}

/**
 * Tries to retrieve a variable that may throw an exception
 *
 * @Returns variable if successful else null
 */
inline fun <T : Any?> tryGet(validate: () -> T?): T? {
    return try {
        validate.invoke()
    } catch (e: Exception) {
        null
    }
}