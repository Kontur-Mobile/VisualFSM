package ru.kontur.mobile.visualfsm.annotation_processor.dsl

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.annotation_processor.TestUtil

internal class DSLInvalidGenericParameterAnnotationProcessorTests {

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
                
                sealed class TestAction: DslAction<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                    internal fun transition1() = transition<TestState.TestState1, TestState.TestState2>().transform { TestState.TestState2() }
                    internal fun transition2() = transition<TestState.TestState2, TestState.TestState1>().transform { TestState.TestState1() }
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
        Assertions.assertTrue(result.messages.contains("Super class of \"TestFeature(Test.kt:17)\" contains generic parameter with invalid class name."))
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
                
                sealed class TestAction: DslAction<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                    internal fun transition1() = transition<TestState.TestState1111, TestState.TestState2>().transform { TestState.TestState2() } 
                    internal fun transition2() = transition<TestState.TestState2, TestState.TestState1>().transform { TestState.TestState1() } 
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
        Assertions.assertTrue(result.messages.contains("\"fun transition1(Test.kt:12)\" returns a value with an invalid class name."))
    }
}