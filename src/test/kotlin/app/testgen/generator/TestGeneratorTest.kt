package app.testgen.generator

import app.testgen.model.*
import org.junit.jupiter.api.Test

internal class TestGeneratorTest {

    @Test
    fun gen() {
        val expectedType = Type(
            "kotlin.collections.List",
            typeArgs = listOf(
                Type(
                    "kotlin.collections.List",
                    typeArgs = listOf(
                        Type(
                            "kotlin.collections.List",
                            typeArgs = listOf(Type("kotlin.String"))
                        )
                    )
                )
            )
        )

        val targetClass = ContainingClass("Sample", "com.dena.swet.android.testgen")
        val function = FunctionSignature("sample", listOf(
            Arg(
                expectedType,
                "text"
            )
        ), ReturnType(expectedType)
        )

        val target = DefaultTargetFunction(targetClass, function)

        val generator = DynamicTestGenerator()
        val generatedTest = generator.gen(target)

        println(generatedTest.fileSpec)
    }
}