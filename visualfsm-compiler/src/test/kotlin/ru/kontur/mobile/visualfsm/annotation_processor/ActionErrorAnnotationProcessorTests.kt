package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ActionErrorAnnotationProcessorTests : AnnotationProcessorTests() {

    @Test
    fun testBaseActionMustBeSealed() {
        val testActionSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedActionFactoryProvider
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                class TestAction: Action<TestState>()
                
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
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("Base Action class must be sealed. The \"TestAction\" does not meet this requirement."))
    }

    @Test
    fun testBaseActionMustHaveSealedSubclasses() {
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
                
                open class TestAction1(val parameter1: String) {
                
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
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("Base action class must have sealed subclasses"))
    }

    @Test
    fun testAllActionConstructorParametersMustBeDeclaredAsProperties() {
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
                
                open class TestAction1(val parameter1: String, parameter2: String, var parameter3: String): TestAction() {
                
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
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("All action constructor parameters must be declared as properties (have the var or val keyword). The \"parameter2\" parameter in the \"TestAction1\" constructor does not meet this requirement."))
    }

}