package app.testgen.psiparser

import app.testgen.TestGenResult
import app.testgen.model.DefaultTargetFunction
import app.testgen.model.TargetFunction
import app.testgen.psiparser.element.ClassOrObjectParser
import app.testgen.psiparser.element.FileNameParser
import app.testgen.psiparser.element.NamedFunctionParser
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isExtensionDeclaration

class DefaultPsiParser : PsiParser {

    override fun parse(psiFile: PsiFile, offset: Int): TestGenResult<TargetFunction> {

        if (psiFile !is KtFile) {
            return TestGenResult.failure(PsiParseException("Only support for Kotlin file"))
        }

        val element = psiFile.findElementAt(offset)

        val namedFunction =
            PsiTreeUtil.getParentOfType(element, KtNamedFunction::class.java)
                ?: return TestGenResult.failure(PsiParseException("Only support for Kotlin named function"))

        val containingClassOrObject = namedFunction.containingClassOrObject

        return when {
            namedFunction.hasTypeParameterListBeforeFunctionName() -> {
                return TestGenResult.failure(PsiParseException("Type parameter function in not supported yet"))
            }
            namedFunction.isExtensionDeclaration() -> {
                return TestGenResult.failure(PsiParseException("Extension function in not supported yet"))
            }
            namedFunction.isTopLevel -> {
                executeParse {
                    DefaultTargetFunction(
                        FileNameParser().parse(psiFile),
                        NamedFunctionParser().parse(namedFunction)
                    )
                }
            }
            containingClassOrObject != null -> {
                executeParse {
                    DefaultTargetFunction(
                        ClassOrObjectParser().parse(containingClassOrObject),
                        NamedFunctionParser().parse(namedFunction)
                    )
                }
            }
            else -> {
                return TestGenResult.failure(PsiParseException("The function signature is not supported "))
            }
        }
    }
}