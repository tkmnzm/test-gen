package app.testgen.output

import app.testgen.TestGenResult
import app.testgen.model.GeneratedTest
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

interface TestFileWriter {
    fun write(psiFile: PsiFile, project: Project, generatedTest: GeneratedTest): TestGenResult<PsiFile>
}