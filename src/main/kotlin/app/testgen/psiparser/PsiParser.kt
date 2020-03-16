package app.testgen.psiparser

import app.testgen.TestGenResult
import app.testgen.model.TargetFunction
import com.intellij.psi.PsiFile

interface PsiParser {
    fun parse(psiFile: PsiFile, offset: Int): TestGenResult<TargetFunction>
}

fun executeParse(func: () -> TargetFunction): TestGenResult<TargetFunction> {
    return try {
        TestGenResult.success(func())
    } catch (e: Throwable) {
        TestGenResult.failure(e)
    }
}