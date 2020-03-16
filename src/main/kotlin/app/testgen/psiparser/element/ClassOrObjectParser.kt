package app.testgen.psiparser.element

import app.testgen.model.Containing
import app.testgen.model.ContainingClass
import app.testgen.model.ContainingObject
import app.testgen.psiparser.PsiParseException
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.psiUtil.containingClass

class ClassOrObjectParser(private val valueParametersParser: ValueParametersParser = ValueParametersParser()) {
    fun parse(psiClassOrObject: KtClassOrObject): Containing {

        val name = psiClassOrObject.name ?: throw PsiParseException("Parent class or object name is null")
        val packageName =  KtPsiUtil.getPackageName(psiClassOrObject) ?: ""

        return if(psiClassOrObject is KtObjectDeclaration) {
            val containingClass = psiClassOrObject.containingClass()
            ContainingObject(containingClass?.name ?: name, packageName)
        } else {
            val constructorArgs = valueParametersParser.parse(psiClassOrObject.primaryConstructor?.valueParameters)
            ContainingClass(name, packageName, constructorArgs)
        }
    }
}