package app.testgen.output

import app.testgen.TestGenResult
import app.testgen.model.GeneratedTest
import com.intellij.ide.util.DirectoryChooserUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

class DefaultTestFileWriter(private val testDirFinder: TestDirFinder = TestDirFinder()) : TestFileWriter
{
    override fun write(psiFile: PsiFile, project: Project, generatedTest: GeneratedTest): TestGenResult<PsiFile> {

        val testDirs = testDirFinder.find(project , psiFile)

        if(testDirs.isEmpty()) {
            return TestGenResult.failure(WriteTestFileFailureException("no suitable test module"))
        }

        val testRootDir = DirectoryChooserUtil.selectDirectory(project, testDirs.toTypedArray(), testDirs.first(), "")

        val packageName = (psiFile as KtFile).packageFqName
        val testKtFile = KtPsiFactory(project).createFile("${generatedTest.fileSpec.name}.kt", generatedTest.fileSpec.toString())

        CodeStyleManager.getInstance(project).reformat(testKtFile)

        var current: PsiDirectory? = testRootDir

        val dir = packageName.pathSegments().map {
            val subDir = current?.findSubdirectory(it.asString())
            current = subDir ?: current?.createSubdirectory(it.asString())
            current
        }.lastOrNull() ?: testRootDir

        return when (val originFile = dir?.findFile(generatedTest.fileSpec.name)) {
            null -> {
                dir?.add(testKtFile)
                TestGenResult.success(testKtFile)
            }
            is KtFile -> {
                originFile.appendNamedFunction(testKtFile)
                TestGenResult.success(originFile)
            }
            else -> {
                TestGenResult.failure(WriteTestFileFailureException("same name file exit(but not kotlin)"))
            }
        }
    }
}