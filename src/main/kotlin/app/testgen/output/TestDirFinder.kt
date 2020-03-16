package app.testgen.output

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.JavaProjectRootsUtil
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.SourceFolder
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.testIntegration.createTest.CreateTestAction
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.kotlin.idea.refactoring.toPsiDirectory

class TestDirFinder {

    fun find(project: Project, psiFile: PsiFile) : List<PsiDirectory>{
        val srcModule = ModuleUtilCore.findModuleForFile(psiFile)

        val testModule = CreateTestAction.suggestModuleForTests(project, srcModule!!)
        val testRootUrls = computeTestRoots(testModule)

        if (testRootUrls.isEmpty() && computeSuitableTestRootUrls(testModule).isEmpty()) {
            return emptyList()
        }

        return testRootUrls.mapNotNull { it.toPsiDirectory(project) }
    }

    fun computeSuitableTestRootUrls(module: Module): List<String> {
        return suitableTestSourceFolders(module).map { it.url }
    }

    fun computeTestRoots(mainModule: Module): List<VirtualFile> {
        if (computeSuitableTestRootUrls(mainModule).isNotEmpty()) {
            return suitableTestSourceFolders(mainModule).mapNotNull { it.file }
        }

        //suggest to choose from all dependencies modules
        val modules = HashSet<Module>()
        ModuleUtilCore.collectModulesDependsOn(mainModule, modules)
        return modules
            .flatMap { suitableTestSourceFolders(it) }.mapNotNull { it.file }
    }

    fun suitableTestSourceFolders(module: Module): List<SourceFolder> {
        return ModuleRootManager.getInstance(module).contentEntries
            .flatMap { entry -> entry.getSourceFolders(JavaSourceRootType.TEST_SOURCE) }
            .filterNot { src -> JavaProjectRootsUtil.isForGeneratedSources(src) }
    }
}


