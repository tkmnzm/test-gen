package app.testgen.generator

import app.testgen.model.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.junit.jupiter.api.TestFactory

class DynamicTestGenerator : TestGenerator {

    override fun gen(targetFunction: TargetFunction): DefaultGeneratedTest {

        targetFunction as DefaultTargetFunction //TODO　エラーハンドリング

        val testFunctionBuilder = FunSpec.builder("test${targetFunction.function.name.capitalize()}")
            .returns(collection.parameterizedBy(dynamicTest))
            .addAnnotation(TestFactory::class.java)

        val testCaseClassConstructorBuilder = FunSpec.constructorBuilder()

        val testCaseClass = TypeSpec.classBuilder("TestCase")
            .addModifiers(KModifier.DATA)

        targetFunction.function.args.forEach {

            val typeName = resolveClassName(it.type)

            testCaseClassConstructorBuilder.addParameter(it.name, typeName)
            testCaseClass.addProperty(
                PropertySpec.builder(it.name, typeName)
                    .initializer(it.name).build()
            )
        }

        if (targetFunction.function.returnType?.type != null && targetFunction.function.returnType.type.name != "Unit") {

            val type = resolveClassName(targetFunction.function.returnType.type)
            testCaseClassConstructorBuilder.addParameter("expect", type)
            testCaseClass.addProperty(
                PropertySpec.builder("expect", type)
                    .initializer("expect").build()
            )
        }

        if (targetFunction.parent is ContainingClass && targetFunction.parent.args.isNotEmpty()) {

            val dependenciesClassConstructorBuilder = FunSpec.constructorBuilder()

            val dependencyClass = TypeSpec.classBuilder("Dependencies")
                .addModifiers(KModifier.DATA)

            targetFunction.parent.args.forEach {
                val type = resolveClassName(it.type)

                dependenciesClassConstructorBuilder.addParameter(it.name, type)
                dependencyClass.addProperty(
                    PropertySpec.builder(it.name, type)
                        .initializer(it.name).build()
                )
            }

            dependencyClass.primaryConstructor(
                dependenciesClassConstructorBuilder.build()
            )


            val dependencyClassName = ClassName(targetFunction.parent.packageName, "Dependencies")

            testCaseClassConstructorBuilder.addParameter(
                "dependencies",
                dependencyClassName
            )

            testCaseClass.addProperty(
                PropertySpec.builder("dependencies", dependencyClassName)
                    .initializer("dependencies").build()
            )

            testFunctionBuilder.addStatement("%L", dependencyClass.build())
        }

        val actStatement = buildCodeBlock {

            if (targetFunction.parent is ContainingClass) {
                add("val model = %L(", targetFunction.parent.name)

                val arg = targetFunction.parent.args.joinToString(",\n") {
                    "case.dependencies.${it.name}"
                }
                add("%L", arg)
                add("%L", ")\n")
                add("%L", "\n")

                if (targetFunction.function.returnType?.type != null && targetFunction.function.returnType.type.name != "Unit") {
                    add("%L", "val actual = ")
                }
                add("model.%L(", targetFunction.function.name)
            } else if (targetFunction.parent is ContainingObject) {
                if (targetFunction.function.returnType?.type != null && targetFunction.function.returnType.type.name != "Unit") {
                    add("%L", "val actual = ")
                }
                add("%L(", "${targetFunction.parent.name}.${targetFunction.function.name}")
            } else if (targetFunction.parent is ContainingFile) {
                if (targetFunction.function.returnType?.type != null && targetFunction.function.returnType.type.name != "Unit") {
                    add("%L", "val actual = ")
                }
                add("%L(", targetFunction.function.name)
            }

            val arg = targetFunction.function.args.joinToString(",\n") {
                "case.${it.name}"
            }
            add("%L", arg)
            add("%L", ")\n")
        }


        val assertStatement = buildCodeBlock {
            if (targetFunction.function.returnType?.type != null && targetFunction.function.returnType.type.name != "Unit") {
                add("%L", "assertThat(actual, equalTo(case.expect))")
            } else {
                add("%L", "//assertion")
            }
        }

        val dynamicTestStatement = buildCodeBlock {
            add("%L", "return listOf<TestCase>(\n")
            indent()
            add("%L", "//TODO add your test\n")
            unindent()
            add("%L", ").map { case ->\n")
            indent()
            add("%L", "dynamicTest(case.toString()) {\n")
            indent()
            add("%L", "$actStatement\n")
            add("%L", "$assertStatement\n")
            unindent()
            add("%L", "}\n")
            unindent()
            add("%L", "}")
        }


        testCaseClass
            .primaryConstructor(
                testCaseClassConstructorBuilder.build()
            )

        testFunctionBuilder.addStatement("%L", testCaseClass.build())
            .addStatement("%L", dynamicTestStatement)

        val content = FileSpec.builder(targetFunction.parent.packageName, "${targetFunction.parent.name}Test")
            .addImport("org.hamcrest.MatcherAssert", "assertThat")
            .addImport("org.hamcrest.CoreMatchers", "equalTo")
            .addImport("org.junit.jupiter.api.DynamicTest", "dynamicTest")
            .addType(
                TypeSpec.classBuilder("${targetFunction.parent.name}Test")
                    .addFunction(
                        testFunctionBuilder.build()
                    )
                    .build()
            )
            .build()

        return DefaultGeneratedTest(content)
    }

    private fun resolveClassName(type: Type): TypeName {

        if (type.typeArgs.isEmpty()) {
            return ClassName.bestGuess(type.name)
        }

        val classType = ClassName.bestGuess(type.name)
        return classType.parameterizedBy(type.typeArgs.map { typeArg -> resolveClassName(typeArg) })
    }
}