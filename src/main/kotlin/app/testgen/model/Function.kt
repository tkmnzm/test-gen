package app.testgen.model


data class Arg(val type: Type, val name: String)

data class ReturnType(val type: Type, val name: String? = null)

data class Type(val name: String, val typeArgs: List<Type> = emptyList())

sealed class TargetFunction

data class DefaultTargetFunction(
    val parent: Containing,
    val function: FunctionSignature
) : TargetFunction()

data class FunctionSignature(
    val name: String,
    val args: List<Arg> = emptyList(),
    val returnType: ReturnType? = null
)

sealed class Containing(
    open val name: String,
    open val packageName: String = ""
)

data class ContainingClass(
    override val name: String,
    override val packageName: String = "",
    val args: List<Arg> = emptyList()
) : Containing(name, packageName)

data class ContainingFile(
    override val name: String,
    override val packageName: String = ""
) : Containing(name, packageName)

data class ContainingObject(
    override val name: String,
    override val packageName: String = ""
) : Containing(name, packageName)