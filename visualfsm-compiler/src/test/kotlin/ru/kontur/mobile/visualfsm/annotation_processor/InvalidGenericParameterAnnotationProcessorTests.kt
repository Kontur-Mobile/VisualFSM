package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class InvalidGenericParameterAnnotationProcessorTests {

    @Test
    fun testInvalidFeatureGenericParameter() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestState1, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    inner class Transition2: Transition<TestState.TestState2, TestState.TestState1>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                }
                
                @GenerateTransitionsFactory
                class TestFeature: Feature<TestStatee, TestAction>(
                    initialState = TestState.TestState1(),
                    transitionsFactory = provideTransitionsFactory(),
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("Super class of \"TestFeature(Test.kt:24)\" contains generic parameter with invalid class name."))
    }

    @Test
    fun testInvalidTransitionGenericParameter() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestState1111, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    inner class Transition2: Transition<TestState.TestState2, TestState.TestState1>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                }
                
                @GenerateTransitionsFactory
                class TestFeature: Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    transitionsFactory = provideTransitionsFactory(),
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("Super class of \"TestAction1.Transition1(Test.kt:13)\" contains generic parameter with invalid class name."))
    }
}