package app.testgen.action

import app.testgen.TestGen
import app.testgen.generator.DynamicTestGenerator
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class TestGenActionGroup : ActionGroup() {

    override fun isPopup(): Boolean {
        return true
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        if (e == null) {
            return AnAction.EMPTY_ARRAY;
        }
        return arrayOf(
            TestGenAction(
                "Generate DynamicTest",
                TestGen(generator = DynamicTestGenerator())
            )
        )
    }
}