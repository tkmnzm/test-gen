package app.testgen.generator

import app.testgen.model.GeneratedTest
import app.testgen.model.TargetFunction

interface TestGenerator {
    fun gen(targetFunction: TargetFunction): GeneratedTest
}