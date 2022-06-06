package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class FeatureErrorAnnotationProcessorTests : AnnotationProcessorTests() {

    @Test
    fun testClassWithProceedsGeneratedActionsAnnotationNotInheritedFromFeature() {
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
                class TestFeature
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = visualFSMSources + testActionSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("Only class inherited from class ru.kontur.mobile.visualfsm.Feature or class ru.kontur.mobile.visualfsm.rxjava3.FeatureRx or class ru.kontur.mobile.visualfsm.rxjava2.FeatureRx can be annotated with @ru.kontur.mobile.visualfsm.ProceedsGeneratedActions. The \"TestFeature\" does not meet this requirement."))
    }

    @Test
    fun testClassWithProceedsGeneratedActionsAnnotationMustHaveParentWithTwoGenericParams() {
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

                abstract class FeatureAbstract: Feature<TestState, TestAction>
                
                @ProceedsGeneratedActions
                class TestFeature: FeatureAbstract(
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
        Assertions.assertTrue(result.messages.contains("Super class of feature must have exactly two generic types (state and action). But the super class of \"TestFeature\" has 0: []"))
    }

}