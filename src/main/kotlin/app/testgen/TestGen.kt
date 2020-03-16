package app.testgen

import app.testgen.generator.TestGenerator
import app.testgen.model.GeneratedTest
import app.testgen.psiparser.DefaultPsiParser
import app.testgen.psiparser.PsiParser
import com.intellij.psi.PsiFile

class TestGen(
    val parser: PsiParser = DefaultPsiParser(),
    val generator: TestGenerator
) {

    fun process(psiFile: PsiFile, offset: Int): TestGenResult<GeneratedTest> {
        return when (val result = parser.parse(psiFile, offset)) {
            is TestGenResult.Success -> {
                TestGenResult.success(generator.gen(result.data))
            }
            is TestGenResult.Failure -> {
                TestGenResult.failure(result.throwable)
            }
        }
    }
}