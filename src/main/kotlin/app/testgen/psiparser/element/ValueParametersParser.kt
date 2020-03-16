package app.testgen.psiparser.element

import app.testgen.model.Arg
import app.testgen.psiparser.PsiParseException
import app.testgen.psiparser.resolveTypeReference
import org.jetbrains.kotlin.psi.KtParameter

class ValueParametersParser {
    fun parse(valueParameters: List<KtParameter>?): List<Arg> {

        return valueParameters?.map { ktParam ->
            val typeRef = ktParam.typeReference
                ?: throw PsiParseException("Parameter type reference is null")
            Arg(resolveTypeReference(typeRef), ktParam.nameAsSafeName.identifier)

        } ?: emptyList()
    }
}