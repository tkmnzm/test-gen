package app.testgen.action

import app.testgen.TestGen
import app.testgen.TestGenResult
import app.testgen.model.GeneratedTest
import app.testgen.output.DefaultTestFileWriter
import app.testgen.output.TestFileWriter
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtFile


class TestGenAction(
    private val name: String = "Test Gen",
    private val generator: TestGen,
    private val writer: TestFileWriter = DefaultTestFileWriter()
) : AnAction() {

    override fun actionPerformed(anActionEvent: AnActionEvent) {

        val project = anActionEvent.getData(CommonDataKeys.PROJECT)
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR)
        val psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE)

        if (editor == null || psiFile == null || project == null) return

        val offset = editor.caretModel.offset

        when (val result = generator.process(psiFile, offset)) {
            is TestGenResult.Success -> {
                WriteCommandAction.runWriteCommandAction(project) {
                    writeTestFile(project, psiFile, result.data)
                }
            }
            is TestGenResult.Failure -> {
                Notifications.Bus.notify(
                    Notification(
                        "TestGenAction", "TestGen", result.throwable.message ?: "gen test error", NotificationType.ERROR
                    )
                )
            }
        }
    }

    fun writeTestFile(project: Project, psiFile: PsiFile, generatedTest: GeneratedTest) {

        when (val result = writer.write(psiFile, project, generatedTest)) {
            is TestGenResult.Failure -> {
                Notifications.Bus.notify(
                    Notification(
                        "TestGenAction", "TestGen", result.throwable.message ?: "gen test error", NotificationType.ERROR
                    )
                )
            }
        }

    }

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        val presentation = e.presentation
        presentation.text = name
        presentation.isEnabledAndVisible = file is KtFile
    }
}


