package app.testgen.psiparser

import app.testgen.model.Type
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlinx.serialization.compiler.resolve.toClassDescriptor

fun resolveTypeReference(typeReference: KtTypeReference): Type {
    val bindingContext: BindingContext = typeReference.analyze()

    val ktType: KotlinType = bindingContext[BindingContext.TYPE, typeReference]
        ?: throw PsiParseException("KotlinType is null")

    return resolveType(ktType, typeReference.text)
}

fun resolveType(ktType: KotlinType, defaultName: String): Type { //TODO エラーハンドリング
    val typeName = ktType.toClassDescriptor?.fqNameSafe?.toString() ?: defaultName
    val typeArgs = ktType.arguments.map {
        resolveType(it.type, it.type.toString())
    }
    return Type(typeName, typeArgs)
}