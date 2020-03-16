package app.testgen.psiparser

import app.testgen.TestGenResult
import app.testgen.model.*
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.kotlin.psi.KtFile

class DefaultPsiParserTest() {
    class TopLevelFunctionTest : LightCodeInsightFixtureTestCase() {

        private val fileName = "TopLevelFunction.kt"

        override fun getTestDataPath(): String {
            return "testdata"
        }

        fun testWithReturnValue() {
            testParseFunctionAtPosition(
                LogicalPosition(3, 2),
                FunctionSignature(
                    "withReturnValue",
                    listOf(Arg(Type("kotlin.String"), "text")),
                    ReturnType(Type("kotlin.String"))
                )
            )
        }

        fun testWithNoReturnValue() {
            testParseFunctionAtPosition(
                LogicalPosition(7, 0),
                FunctionSignature(
                    "noReturnValue",
                    listOf(Arg(Type("kotlin.String"), "text")),
                    null
                )
            )
        }

        fun testWithNoArgAndNoReturnValue() {
            testParseFunctionAtPosition(
                LogicalPosition(10, 0),
                FunctionSignature(
                    "none",
                    listOf(),
                    null
                )
            )
        }

        private fun testParseFunctionAtPosition(logicalPosition: LogicalPosition, signature: FunctionSignature) {

            val result = parseTestFile(logicalPosition) as DefaultTargetFunction

            assertThat(
                result, equalTo(
                    DefaultTargetFunction(
                        ContainingFile("TopLevelFunction", "com.dena.swet.android.testgen"),
                        signature
                    )
                )
            )
        }

        private fun parseTestFile(logicalPosition: LogicalPosition): TargetFunction {
            val psiFile = myFixture.configureByFile(fileName) as KtFile

            editor.caretModel.moveToLogicalPosition(logicalPosition)
            val offset = editor.caretModel.offset

            val parser = DefaultPsiParser()
            val result = parser.parse(psiFile, offset) as TestGenResult.Success
            return result.data
        }
    }

    class ClassFunctionTest : LightCodeInsightFixtureTestCase() {

        private val fileName = "ClassFunction.kt"

        override fun getTestDataPath(): String {
            return "testdata"
        }

        fun testWithNone() {
            testParseNoArgClassAtPosition(
                60,
                FunctionSignature(
                    "none",
                    emptyList(),
                    null
                )
            )
        }

        fun testWithNoReturnValue() {
            testParseNoArgClassAtPosition(
                84,
                FunctionSignature(
                    "noReturnValue",
                    listOf(Arg(Type("kotlin.String"), "text")),
                    null
                )
            )
        }


        fun testWithReturnValue() {
            testParseNoArgClassAtPosition(
                133,
                FunctionSignature(
                    "withReturnValue",
                    listOf(Arg(Type("kotlin.String"), "text")),
                    ReturnType(Type("kotlin.String"))
                )
            )
        }

        fun testArgClassWithReturnValue() {
            testParseArgClassAtPosition(
                253,
                FunctionSignature(
                    "withReturnValue",
                    listOf(Arg(Type("kotlin.Int"), "value")),
                    ReturnType(Type("kotlin.String"))
                )
            )
        }

        private fun testParseNoArgClassAtPosition(offset: Int, signature: FunctionSignature) {

            val result = parseTestFile(offset) as DefaultTargetFunction

            assertThat(
                result, equalTo(
                    DefaultTargetFunction(
                        ContainingClass("NoArg", "com.dena.swet.android.testgen"),
                        signature
                    )
                )
            )
        }

        private fun testParseArgClassAtPosition(offset: Int, signature: FunctionSignature) {

            val result = parseTestFile(offset) as DefaultTargetFunction

            assertThat(
                result, equalTo(
                    DefaultTargetFunction(
                        ContainingClass(
                            "Arg",
                            "com.dena.swet.android.testgen",
                            listOf(
                                Arg(Type("kotlin.String"), "text"),
                                Arg(Type("kotlin.Int"), "num")
                            )
                        ),
                        signature
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

    class ObjectFunctionTest : LightCodeInsightFixtureTestCase() {

        private val fileName = "Object.kt"

        override fun getTestDataPath(): String {
            return "testdata"
        }

        fun testWithReturnValue() {

            val result = parseTestFile(67) as DefaultTargetFunction

            assertThat(
                result, equalTo(
                    DefaultTargetFunction(
                        ContainingObject(
                            "Singleton",
                            "com.dena.swet.android.testgen"
                        ),
                        FunctionSignature(
                            "staticFunc"
                        )
                    )
                )
            )
        }

        fun testWithNoReturnValue() {
            val result = parseTestFile(142) as DefaultTargetFunction

            assertThat(
                result, equalTo(
                    DefaultTargetFunction (
                        ContainingObject(
                            "Static",
                            "com.dena.swet.android.testgen"
                        ),
                        FunctionSignature(
                            "staticFunc"
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
}