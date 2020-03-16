package app.testgen.output

import com.google.common.truth.Truth
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

internal class TestTestFileMergeTest : LightCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String {
        return "testdata"
    }

    fun testProcess() {

        val origin = myFixture.configureByFile("MergeTestFileOrigin.kt") as KtFile
        val source = myFixture.configureByFile("MergeTestFileSource.kt") as KtFile

        WriteCommandAction.runWriteCommandAction(project) {
            val result = origin.appendNamedFunction(source)

            Truth.assertThat(result.importDirectives.map { it.text })
                .containsExactly(
                    "import org.junit.jupiter.api.DynamicTest",
                    "import org.junit.jupiter.api.TestFactory",
                    "import java.util.Collection",
                    "import java.util.Date"
                )

            val namedFunctions = result.collectDescendantsOfType<KtNamedFunction>()

            Truth.assertThat(namedFunctions.map { it.name })
                .containsExactly(
                    "origin1",
                    "origin2",
                    "source1"
                )
        }
    }
}