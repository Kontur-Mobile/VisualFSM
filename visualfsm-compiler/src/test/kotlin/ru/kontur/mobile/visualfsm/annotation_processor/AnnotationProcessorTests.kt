package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.*
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass

internal open class AnnotationProcessorTests {

    protected val visualFSMSources = listOf(
        getSource(Action::class),
        getSource(Transition::class),
        getSource(State::class),
        getSource(Feature::class),
        getSource(ProceedsGeneratedActions::class),
    )

    @Test
    fun testNoErrors() {
        val testActionSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedActionFactoryProvider
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                sealed class TestAction: Action<TestState>()
                
                open class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestState1, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    inner class Transition2: Transition<TestState.TestState2, TestState.TestState1>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                }
                
                @ProceedsGeneratedActions
                class TestFeature: Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    generatedActionFactory = GeneratedActionFactoryProvider.provide()
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = visualFSMSources + testActionSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspGeneratedSources()
        val generatedTestActionFactory = kspGeneratedSources.first { it.path.endsWith("GeneratedTestActionFactory.kt") }
        val testActionImpl = kspGeneratedSources.first { it.path.endsWith("TestAction1Impl.kt") }
        Assertions.assertEquals(
            "import ru.kontur.mobile.visualfsm.GeneratedActionFactory\n" +
                    "\n" +
                    "public class GeneratedTestActionFactory : GeneratedActionFactory<TestAction>() {\n" +
                    "  public override fun create(action: TestAction) = when (action) {\n" +
                    "      is TestAction1 -> TestAction1Impl(action.parameter1)\n" +
                    "      else -> throw IllegalStateException(\"All sealed subclasses of base Action must be handled in when\")\n" +
                    "  }\n" +
                    "\n" +
                    "}\n",
            generatedTestActionFactory.readText()
        )
        Assertions.assertEquals(
            "import kotlin.String\n" +
                    "import kotlin.Suppress\n" +
                    "\n" +
                    "public class TestAction1Impl(\n" +
                    "  parameter1: String,\n" +
                    ") : TestAction1(parameter1) {\n" +
                    "  @Suppress(\"OverridingDeprecatedMember\", \"OVERRIDE_DEPRECATION\")\n" +
                    "  public override fun getTransitions() = listOf(\n" +
                    "      Transition1().apply {\n" +
                    "          fromState = TestState.TestState1::class\n" +
                    "          toState = TestState.TestState2::class\n" +
                    "      },\n" +
                    "      Transition2().apply {\n" +
                    "          fromState = TestState.TestState2::class\n" +
                    "          toState = TestState.TestState1::class\n" +
                    "      },\n" +
                    "  )\n" +
                    "}\n",
            testActionImpl.readText()
        )
    }

    private fun getSource(kClass: KClass<*>): SourceFile {
        val actionAnnotationRelativePath = kClass.qualifiedName!!.toCharArray().joinToString(
            separator = "",
            transform = { if (it.toString() == ".") File.separator else it.toString() }
        )

        val parentPath = Paths.get("").toAbsolutePath().parent.toAbsolutePath().toString()

        val path = StringBuilder()
            .append(parentPath)
            .append(File.separator)
            .append("visualfsm-core")
            .append(File.separator)
            .append("src")
            .append(File.separator)
            .append("commonMain")
            .append(File.separator)
            .append("kotlin")
            .append(File.separator)
            .append(actionAnnotationRelativePath)
            .append(".kt")
            .toString()

        return SourceFile.fromPath(File(path))
    }

    private fun KotlinCompilation.Result.getWorkingDir(): File = outputDirectory.parentFile!!

    private fun KotlinCompilation.Result.getKspGeneratedSources(): List<File> {
        val kspWorkingDir = getWorkingDir().resolve("ksp")
        val kspGeneratedDir = kspWorkingDir.resolve("sources")
        val kotlinGeneratedDir = kspGeneratedDir.resolve("kotlin")
        return kotlinGeneratedDir.walkTopDown().toList() - kotlinGeneratedDir
    }

}