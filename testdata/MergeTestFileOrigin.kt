package com.dena.swet.android.testgen

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.Collection

class MergeTest {

    @TestFactory
    fun origin1(): Collection<DynamicTest> {
        return emptyList()
    }

    @TestFactory
    fun origin2(): Collection<DynamicTest> {
        return emptyList()
    }
}
