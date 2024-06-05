package ru.kontur.mobile.visualfsm.annotation_processor.dsl

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.annotation_processor.TestUtil
import ru.kontur.mobile.visualfsm.annotation_processor.TestUtil.getKspNoCodeGeneratedSources

internal class DSLAllTransitionsFileTests {
    @Test
    fun testAllTransitionsFile() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                    class TestState3: TestState()
                }
                
                sealed class TestAction: DslAction<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                    internal fun transition11() = transition<TestState.TestState1, TestState.TestState2>.transform{ TestState.TestState2() }
                    internal fun transition12() = transition<TestState.TestState2, TestState.TestState1>.transform{ TestState.TestState1() }
                }

                class TestAction2(val parameter1: String): TestAction() {
                    internal fun transition21() = transition<TestState.TestState1, TestState.TestState2>.transform { TestState.TestState2() }
                    internal fun transition22() = transition<TestState.TestState2, TestState.TestState1>.transform { TestState.TestState1() }
                    internal fun transition23() = transition<TestState.TestState3, TestState.TestState2>.transform { TestState.TestState2() }

                    @Edge("NamedByEdgeTransition")
                    internal fun transition24() = transition<TestState.TestState2, TestState.TestState3>.transform { TestState.TestState1() }
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
            kspArgs = mutableMapOf(
                "generateAllTransitionsCsvFiles" to "true",
            )
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspNoCodeGeneratedSources()
        val generatedAllTransitionsFile = kspGeneratedSources.first { it.path.endsWith("TestStateAllTransitions.csv") }
        println(generatedAllTransitionsFile.readText())
        Assertions.assertEquals(
            "transition11,TestState1,TestState2\n" +
                "transition12,TestState2,TestState1\n" +
                "transition21,TestState1,TestState2\n" +
                "transition22,TestState2,TestState1\n" +
                "transition23,TestState3,TestState2\n" +
                "NamedByEdgeTransition,TestState2,TestState3",
            generatedAllTransitionsFile.readText()
        )
    }

    @Test
    fun testTransitionsWithSealedClassInFromStateFile() {
        val testFSMSource = SourceFile.kotlin(
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()

                    sealed class TestNavigation: TestState(){
                        data object NavigateToExit:  TestNavigation()                      
                        data object NavigateToNext:  TestNavigation()

                        sealed class DialogNavigation: TestNavigation(){
                            data object Show: DialogNavigation()
                            data object Hide: DialogNavigation()
                        }
                    }
                }
                
                sealed class TestAction: DslAction<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    internal fun transition1() = transition<TestState.TestNavigation, TestState.TestState1>
                                            .transform { TestState.TestState1() }

                    internal fun transition2() = transition<TestState.TestState1, TestState.TestNavigation.DialogNavigation.Show>
                                            .transform { TestState.TestNavigation.DialogNavigation.Show() }

                    internal fun transition3() = transition<TestState.TestNavigation.DialogNavigation.Show, TestState.TestNavigation.DialogNavigation.Hide>
                                            .transform { TestState.TestNavigation.DialogNavigation.Hide() }
                }
                
                @GenerateTransitionsFactory
                class TestFeature: Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    transitionsFactory = provideTransitionsFactory(),
                )
                """,
            name = "Test.kt"
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
            kspArgs = mutableMapOf(
                "generateAllTransitionsCsvFiles" to "true",
            )
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspNoCodeGeneratedSources()
        val generatedAllTransitionsFile = kspGeneratedSources.first { it.path.endsWith("TestStateAllTransitions.csv") }
        println(generatedAllTransitionsFile.readText())
        Assertions.assertEquals(
            "transition1,TestNavigation.DialogNavigation.Hide,TestState1\n" +
                "transition1,TestNavigation.DialogNavigation.Show,TestState1\n" +
                "transition1,TestNavigation.NavigateToExit,TestState1\n" +
                "transition1,TestNavigation.NavigateToNext,TestState1\n" +
                "transition2,TestState1,TestNavigation.DialogNavigation.Show\n" +
                "transition3,TestNavigation.DialogNavigation.Show,TestNavigation.DialogNavigation.Hide",
            generatedAllTransitionsFile.readText()
        )
    }

    @Test
    fun testTransitionsWithSealedClassInFromStateAndToStateFile() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()

                    sealed class SealedTest1: TestState(){
                        data object TestState2:  SealedTest1()                      
                        data object TestState3:  SealedTest1()
                    }
                }
                
                sealed class TestAction: DslAction<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    internal fun transition1() = transition<TestState.SealedTest1, TestState.SealedTest1>().transform { state }
                    internal fun transition2() = transition<TestState.SealedTest1, TestState.TestState1>().transform { TestState.TestState1() }
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
            kspArgs = mutableMapOf(
                "generateAllTransitionsCsvFiles" to "true",
            )
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspNoCodeGeneratedSources()
        val generatedAllTransitionsFile = kspGeneratedSources.first { it.path.endsWith("TestStateAllTransitions.csv") }
        println(generatedAllTransitionsFile.readText())
        Assertions.assertEquals(
            "transition1,SealedTest1.TestState2,SealedTest1.TestState2\n" +
                "transition1,SealedTest1.TestState2,SealedTest1.TestState3\n" +
                "transition1,SealedTest1.TestState3,SealedTest1.TestState2\n" +
                "transition1,SealedTest1.TestState3,SealedTest1.TestState3\n" +
                "transition2,SealedTest1.TestState2,TestState1\n" +
                "transition2,SealedTest1.TestState3,TestState1",
            generatedAllTransitionsFile.readText()
        )
    }

    @Test
    fun testTransitionsWithSealedClassAllGenericCombinationFile() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2(
                        val sealedState: SealedState
                    ): TestState()
                        
                    sealed class SealedState: TestState(){
                        data object SealedState1: SealedState()
                        data object SealedState2: SealedState()
                    }
                }
                
                internal sealed class TestAction: DslAction<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                    
                    internal fun transition1() = transition<TestState.SealedState, TestState.TestState1>().transform { TestState.TestState1() }

                    internal fun transition2() = transition<TestState.SealedState, TestState.SealedState>().transform { state }

                    internal fun transition3() = selfTransition<TestState.SealedState>().transform { state }

                    internal fun transition4() = transition<TestState.TestState2, TestState.SealedState>().transform { state.sealedState }
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
            kspArgs = mutableMapOf(
                "generateAllTransitionsCsvFiles" to "true",
            )
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspNoCodeGeneratedSources()
        val generatedAllTransitionsFile = kspGeneratedSources.first { it.path.endsWith("TestStateAllTransitions.csv") }
        println(generatedAllTransitionsFile.readText())
        Assertions.assertEquals(
            "transition1,SealedState.SealedState1,TestState1\n" +
                "transition1,SealedState.SealedState2,TestState1\n" +
                "transition2,SealedState.SealedState1,SealedState.SealedState1\n" +
                "transition2,SealedState.SealedState1,SealedState.SealedState2\n" +
                "transition2,SealedState.SealedState2,SealedState.SealedState1\n" +
                "transition2,SealedState.SealedState2,SealedState.SealedState2\n" +
                "transition3,SealedState.SealedState1,SealedState.SealedState1\n" +
                "transition3,SealedState.SealedState2,SealedState.SealedState2\n" +
                "transition4,TestState2,SealedState.SealedState1\n" +
                "transition4,TestState2,SealedState.SealedState2",
            generatedAllTransitionsFile.readText()
        )
    }
}