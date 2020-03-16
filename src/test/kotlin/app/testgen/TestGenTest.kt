package app.testgen

import app.testgen.generator.DynamicTestGenerator
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import junit.framework.TestCase

internal class TestGenTest : LightCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String {
        return "testdata"
    }

    fun test() {
        val psiFile = myFixture.configureByFile("Object.kt")
        editor.caretModel.moveToOffset(142)
        val offset = editor.caretModel.offset
        val testGen = TestGen(generator = DynamicTestGenerator())
        testGen.process(psiFile, offset)

        TestCase.assertNotNull(psiFile)
    }
}