package app.testgen.psiparser.element

import app.testgen.model.ContainingFile
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiUtil

class FileNameParser {
    fun parse(psiFile: KtFile): ContainingFile {
        val fileName = psiFile.containingFile.name
        val packageName = KtPsiUtil.getPackageName(psiFile) ?: ""
        return ContainingFile(fileName.substring(0, fileName.lastIndexOf('.')), packageName)
    }
}