package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ActionErrorAnnotationProcessorTests {

    @Test
    fun testBaseActionMustBeSealed() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestState1, TestState.TestState2>() {
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
        Assertions.assertTrue(result.messages.contains("Base Action class must be sealed. The \"TestAction(Test.kt:9)\" does not meet this requirement."))
    }

    @Test
    fun testBaseActionMustHaveSubclasses() {
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
                
                class TestAction1(val parameter1: String) {
                
                    inner class Transition1: Transition<TestState.TestState1, TestState.TestState2>() {
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
        Assertions.assertTrue(result.messages.contains("Base action class must have subclasses. The \"TestAction(Test.kt:9)\" does not meet this requirement."))
    }

    @Test
    fun testActionMustContainsTransitionsAsInnerClasses() {
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
                
                class TestAction1(val parameter1: String): TestAction() {}
                
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
        Assertions.assertTrue(result.messages.contains("Action must contains transitions as inner classes. The \"TestAction1(Test.kt:11)\" does not meet this requirement."))
    }

    @Test
    fun testActionMustNotOverrideGetTransitionsFunction() {
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
                
                    @Suppress("OverridingDeprecatedMember")
                    override fun getTransitions(): List<Transition<out TestState, out TestState>> {
                        return listOf(Transition1(), Transition2())
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
        Assertions.assertTrue(result.messages.contains("Action must not override getTransitions function. The \"TestAction1(Test.kt:11)\" does not meet this requirement."))
    }

    @Test
    fun testMustHaveTwoSealedClassInGenericTransitionWhenOneToOneTransitionStrategy() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    sealed class SealedClassState: TestState(){
                        object SealedState1: SealedClassState()
                        object SealedState2: SealedClassState()
                    }
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestState1, TestState.SealedClassState.SealedState1>() {
                        override fun transform(state: TestState.TestState1):TestState.SealedClassState.SealedState1 = TestState.SealedClassState.SealedState1()
                    }
                
                    inner class Transition2: OneToOneSealedTransition<TestState.SealedClassState, TestState.TestState1>() {
                        override fun transform(state: TestState.SealedClassState): TestState.TestState1 = TestState.TestState1()
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
        Assertions.assertTrue(result.messages.contains("When using OneToOneSealedTransition, you must pass 2 sealed classes to generic types. The \"TestAction1.Transition2(Test.kt:20)\" does not meet this requirement."))
    }

    @Test
    fun testMustHaveSealedClassInGenericTransitionWhenManyToManyTransitionStrategy() {
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
                
                    inner class Transition1: ManyToManySealedTransition<TestState.TestState1, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1):TestState.TestState2 = TestState.TestState2()
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
        Assertions.assertTrue(result.messages.contains("When using ManyToManySealedTransition, at least one sealed class is required in a generic types. The \"TestAction1.Transition1(Test.kt:13)\" does not meet this requirement."))
    }

    @Test
    fun testSealedClassInTransition() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    sealed class SealedClassState: TestState(){
                        object SealedState1: SealedClassState()
                        object SealedState2: SealedClassState()
                    }
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.SealedClassState, TestState.TestState1>() {
                        override fun transform(state: TestState.SealedClassState):TestState.TestState1 = TestState.TestState1()
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
        Assertions.assertTrue(result.messages.contains("Transition does not process the sealed class in generic. Use ManyToManySealedTransition or OneToOneSealedTransition. The \"TestAction1.Transition1(Test.kt:16)\" does not meet this requirement."))
    }
}