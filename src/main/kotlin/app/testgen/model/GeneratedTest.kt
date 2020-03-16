package app.testgen.model

import com.squareup.kotlinpoet.FileSpec


sealed class GeneratedTest(
    open val fileSpec: FileSpec
)

data class DefaultGeneratedTest(
    override val fileSpec: FileSpec
) : GeneratedTest(fileSpec)