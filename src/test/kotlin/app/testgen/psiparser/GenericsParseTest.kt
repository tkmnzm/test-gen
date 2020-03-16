package app.testgen.psiparser

import app.testgen.TestGenResult
import app.testgen.model.*
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.jetbrains.kotlin.psi.KtFile

class GenericsParseTest : LightCodeInsightFixtureTestCase() {

    private val fileName = "GenericFunction.kt"

    override fun getTestDataPath(): String {
        return "testdata"
    }

    fun testParseGenericsType() {

        val result = parseTestFile(43) as DefaultTargetFunction

        val expectedType = Type(
            "kotlin.collections.List",
            typeArgs = listOf(Type("kotlin.String"))
        )

        MatcherAssert.assertThat(
            result, CoreMatchers.equalTo(
                DefaultTargetFunction(
                    ContainingFile(
                        "GenericFunction",
                        "com.dena.swet.android.testgen"
                    ),
                    FunctionSignature(
                        "list",
                        listOf(Arg(name = "argList", type = expectedType)),
                        ReturnType(expectedType)
                    )
                )
            )
        )
    }

    fun testParseNestedGenericsType() {

        val result = parseTestFile(184) as DefaultTargetFunction

        val expectedType = Type(
            "kotlin.collections.List",
            typeArgs = listOf(
                Type(
                    "kotlin.collections.List",
                    typeArgs = listOf(Type("kotlin.String"))
                )
            )
        )

        MatcherAssert.assertThat(
            result, CoreMatchers.equalTo(
                DefaultTargetFunction(
                    ContainingFile(
                        "GenericFunction",
                        "com.dena.swet.android.testgen"
                    ),
                    FunctionSignature(
                        "nestList",
                        listOf(Arg(name = "argList", type = expectedType)),
                        ReturnType(expectedType)
                    )
                )
            )
        )
    }

    fun testParseFurtherNestedGenericsType() {

        val result = parseTestFile(301) as DefaultTargetFunction

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

        MatcherAssert.assertThat(
            result, CoreMatchers.equalTo(
                DefaultTargetFunction(
                    ContainingFile(
                        "GenericFunction",
                        "com.dena.swet.android.testgen"
                    ),
                    FunctionSignature(
                        "furtherNestList",
                        listOf(Arg(name = "argList", type = expectedType)),
                        ReturnType(expectedType)
                    )
                )
            )
        )
    }

    private fun parseTestFile(offset: Int): TargetFunction {
        val psiFile = myFixture.configureByFile(fileName) as KtFile

        val parser = DefaultPsiParser()
        val result = parser.parse(psiFile, offset) as TestGenResult.Success
        return result.data
    }
}