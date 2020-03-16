package app.testgen.psiparser.element

import app.testgen.model.FunctionSignature
import app.testgen.model.ReturnType
import app.testgen.psiparser.PsiParseException
import app.testgen.psiparser.resolveTypeReference
import org.jetbrains.kotlin.psi.KtNamedFunction

class NamedFunctionParser(private val valueParametersParser: ValueParametersParser = ValueParametersParser()) {

    fun parse(namedFunction: KtNamedFunction): FunctionSignature {

        val name = namedFunction.name ?: throw PsiParseException("Function name is null")

        // 引数のパース
        val args = valueParametersParser.parse(namedFunction.valueParameters)

        // 戻り値のパース
        val typeRef = namedFunction.typeReference
        val returnType =
            if (typeRef != null) ReturnType(resolveTypeReference(typeRef)) else null

        return FunctionSignature(name, args, returnType)
    }
}