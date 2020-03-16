package app.testgen.output

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.getOrCreateBody
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

fun KtFile.appendNamedFunction(sourceFile: KtFile): KtFile {

    sourceFile.importList?.imports?.filter { sourceImport ->
        this.importDirectives.none { it.text == sourceImport.text }
    }?.map { additionalImport ->
        this.importList?.add(additionalImport)
    }

    val sourceFunc = sourceFile.findDescendantOfType<KtNamedFunction>() ?: return this

    if (this.anyDescendantOfType<KtClass>()) {
        val originClass = this.findDescendantOfType<KtClass>()!!
        val body = originClass.getOrCreateBody()
        body.addBefore(sourceFunc, body.lastChild)
    } else if (sourceFile.anyDescendantOfType<KtClass>()) {
        val sourceClass = sourceFile.findDescendantOfType<KtClass>()!!
        this.add(sourceClass)
    }
    return this
}