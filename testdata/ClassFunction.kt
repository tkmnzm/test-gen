package com.dena.swet.android.testgen

class NoArg() {

    fun none() {
    }

    fun noReturnValue(text: String) {
    }

    fun withReturnValue(text: String): String {
        return ""
    }
}

class Arg(val text: String, val num: Int) {

    fun withReturnValue(value: Int): String {
        return ""
    }

    fun withReturnValue(text: String, num: Int): String {
        return ""
    }
}