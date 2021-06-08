package com.superyao.dev.toolkit.helper

abstract class InitializedHelper {

    protected var initialized = false

    protected fun initCheck() {
        if (!initialized) {
            throw IllegalStateException("$this has not been initialized.")
        }
    }
}