# test gen

テストコードの自動生成をするIntellij Pluginです

## Features

### Dynamic Test

Junit5のDynamic Testを生成します。
Function内の要素のカーソルを当てた状態で、⌘N(Generate) > `Custom Test` > `Generate DynamicTest`を選択する

```kotlin

@TestFactory
fun test(): Collection<DynamicTest> {

   data class TestCase(
       val text: String,
       val expect: Int
   )

   return listOf<TestCase>(
      // TODO add your test
   ).map { case ->
       dynamicTest(case.toString()) {
         ..
       }
   }
}

```

#### まだ対応できていないもの

- 引数・戻り値がFunction Type
- Extension method
- Nested Classのmethod
- Type Parameterをもつmethod




