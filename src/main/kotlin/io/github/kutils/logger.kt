package io.github.kutils

/**
 * @author atistrcsn - 2017
 */

import mu.KLogger
import mu.KotlinLogging
import kotlin.reflect.full.companionObject

inline fun <reified R : Any> R.logging(): KLogger = KotlinLogging.logger { }

fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> =
        if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass) {
            ofClass.enclosingClass
        } else {
            ofClass
        }