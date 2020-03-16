import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class GetSignatureTest {

    @Test
    fun test() {

//        val targetClass =
//            ContainingClass(
//                "com.dena.swet.android.testgen",
//                "Sample"
//            )
//
//        val model = Function(
//            "niko",
//            listOf(Arg("text", "String")),
//            ReturnType("String")
//        )
//
//        val model = TestGen()
//        model.mergeTestFile(targetClass, model)


    }
}

class SampleTest {

    @TestFactory
    fun sample(): Collection<DynamicTest> {

        data class Dependencies(
            val text: String
        )

        data class TestCase(
            val value: Int,
            val expect: String,
            val dependencies: Dependencies
        )

        return listOf<TestCase>(
            //TODO add your test
        ).map { case ->
            dynamicTest(case.toString()) {
                val niko = Sample(case.dependencies.text)
                val actual = niko.value(case.value)
                assertThat(actual, equalTo(case.expect))
            }
        }
    }
}

class Sample(val text: String = "") {

    fun value(value: Int): String {
        return ""
    }

}

