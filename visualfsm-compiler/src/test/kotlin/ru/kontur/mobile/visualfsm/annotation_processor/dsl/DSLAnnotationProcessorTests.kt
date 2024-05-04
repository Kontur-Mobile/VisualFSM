package ru.kontur.mobile.visualfsm.annotation_processor.dsl

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.annotation_processor.TestUtil
import ru.kontur.mobile.visualfsm.annotation_processor.TestUtil.getKspCodeGeneratedSources

internal class DSLAnnotationProcessorTests {
    @Test
    fun testBaseActionWithoutInternalModifier() {
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
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspCodeGeneratedSources()
        val generatedTestStateTransitionsFactory = kspGeneratedSources.first { it.path.endsWith("GeneratedTestFeatureTransitionsFactory.kt") }
        println(generatedTestStateTransitionsFactory.readText())
        Assertions.assertEquals(
            "import kotlin.Suppress\n" +
                "import kotlin.collections.List\n" +
                "import ru.kontur.mobile.visualfsm.Transition\n" +
                "import ru.kontur.mobile.visualfsm.TransitionsFactory\n" +
                "\n" +
                "public class GeneratedTestFeatureTransitionsFactory : TransitionsFactory<TestState, TestAction> {\n" +
                "  @Suppress(\"REDUNDANT_ELSE_IN_WHEN\",\"UNCHECKED_CAST\")\n" +
                "  override fun create(action: TestAction): List<Transition<out TestState, out TestState>> =\n" +
                "      when (action) {\n" +
                "        is TestAction1 -> listOf(\n" +
                "            action.transition1().apply {\n" +
                "                _fromState = TestState.TestState1::class\n" +
                "                _toState = TestState.TestState2::class\n" +
                "            },\n" +
                "            action.transition2().apply {\n" +
                "                _fromState = TestState.TestState2::class\n" +
                "                _toState = TestState.TestState1::class\n" +
                "            },\n" +
                "        )\n" +
                "\n" +
                "        else -> error(\"Code generation error. Not all actions were processed in the when block.\")\n" +
                "    }\n" +
                "}\n",
            generatedTestStateTransitionsFactory.readText()
        )
    }

    @Test
    fun testBaseActionWithInternalModifier() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                internal sealed class TestAction: DslAction<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                    internal fun transition1() = transition<TestState.TestState1, TestState.TestState2>().transform { TestState.TestState2() }
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
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspCodeGeneratedSources()
        val generatedTestStateTransitionsFactory = kspGeneratedSources.first { it.path.endsWith("GeneratedTestFeatureTransitionsFactory.kt") }
        println(generatedTestStateTransitionsFactory.readText())
        Assertions.assertEquals(
            "import kotlin.Suppress\n" +
                "import kotlin.collections.List\n" +
                "import ru.kontur.mobile.visualfsm.Transition\n" +
                "import ru.kontur.mobile.visualfsm.TransitionsFactory\n" +
                "\n" +
                "internal class GeneratedTestFeatureTransitionsFactory : TransitionsFactory<TestState, TestAction> {\n" +
                "  @Suppress(\"REDUNDANT_ELSE_IN_WHEN\",\"UNCHECKED_CAST\")\n" +
                "  override fun create(action: TestAction): List<Transition<out TestState, out TestState>> =\n" +
                "      when (action) {\n" +
                "        is TestAction1 -> listOf(\n" +
                "            action.transition1().apply {\n" +
                "                _fromState = TestState.TestState1::class\n" +
                "                _toState = TestState.TestState2::class\n" +
                "            },\n" +
                "            action.transition2().apply {\n" +
                "                _fromState = TestState.TestState2::class\n" +
                "                _toState = TestState.TestState1::class\n" +
                "            },\n" +
                "        )\n" +
                "\n" +
                "        else -> error(\"Code generation error. Not all actions were processed in the when block.\")\n" +
                "    }\n" +
                "}\n",
            generatedTestStateTransitionsFactory.readText()
        )
    }

    @Test
    fun testBaseActionWithSealedClass() {
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
                
                class TestAction1(val parameter1: String) : TestAction() {
                    internal fun transition1() = transition<TestState.SealedState, TestState.TestState1>().transform { TestState.TestState1()  }
                    internal fun transition2() = transition<TestState.SealedState, TestState.SealedState>().transform { state -> state }
                    internal fun transition3() = selfTransition<TestState.SealedState>().transform { state -> state }
                    internal fun transition4() = transition<TestState.TestState2, TestState.SealedState>().transform { state -> state.sealedState }
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
        println(result.messages)
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspCodeGeneratedSources()
        val generatedTestStateTransitionsFactory = kspGeneratedSources.first { it.path.endsWith("GeneratedTestFeatureTransitionsFactory.kt") }
        println(generatedTestStateTransitionsFactory.readText())
        Assertions.assertEquals(
            "import kotlin.Suppress\n" +
                "import kotlin.collections.List\n" +
                "import ru.kontur.mobile.visualfsm.Transition\n" +
                "import ru.kontur.mobile.visualfsm.TransitionsFactory\n" +
                "\n" +
                "internal class GeneratedTestFeatureTransitionsFactory : TransitionsFactory<TestState, TestAction> {\n" +
                "  @Suppress(\"REDUNDANT_ELSE_IN_WHEN\",\"UNCHECKED_CAST\")\n" +
                "  override fun create(action: TestAction): List<Transition<out TestState, out TestState>> =\n" +
                "      when (action) {\n" +
                "        is TestAction1 -> listOf(\n" +
                "            action.transition1().apply {\n" +
                "                _fromState = TestState.SealedState.SealedState1::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "                _toState = TestState.TestState1::class\n" +
                "            },\n" +
                "            action.transition1().apply {\n" +
                "                _fromState = TestState.SealedState.SealedState2::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "                _toState = TestState.TestState1::class\n" +
                "            },\n" +
                "            action.transition2().apply {\n" +
                "                _fromState = TestState.SealedState.SealedState1::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "                _toState = TestState.SealedState.SealedState1::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "            },\n" +
                "            action.transition2().apply {\n" +
                "                _fromState = TestState.SealedState.SealedState1::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "                _toState = TestState.SealedState.SealedState2::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "            },\n" +
                "            action.transition2().apply {\n" +
                "                _fromState = TestState.SealedState.SealedState2::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "                _toState = TestState.SealedState.SealedState1::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "            },\n" +
                "            action.transition2().apply {\n" +
                "                _fromState = TestState.SealedState.SealedState2::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "                _toState = TestState.SealedState.SealedState2::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "            },\n" +
                "            action.transition3().apply {\n" +
                "                _fromState = TestState.SealedState.SealedState1::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "                _toState = TestState.SealedState.SealedState1::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "            },\n" +
                "            action.transition3().apply {\n" +
                "                _fromState = TestState.SealedState.SealedState2::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "                _toState = TestState.SealedState.SealedState2::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "            },\n" +
                "            action.transition4().apply {\n" +
                "                _fromState = TestState.TestState2::class\n" +
                "                _toState = TestState.SealedState.SealedState1::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "            },\n" +
                "            action.transition4().apply {\n" +
                "                _fromState = TestState.TestState2::class\n" +
                "                _toState = TestState.SealedState.SealedState2::class as kotlin.reflect.KClass<TestState.SealedState>\n" +
                "            },\n" +
                "        )\n" +
                "\n" +
                "        else -> error(\"Code generation error. Not all actions were processed in the when block.\")\n" +
                "    }\n" +
                "}\n",
            generatedTestStateTransitionsFactory.readText()
        )
    }

    @Test
    fun testBaseActionMustIgnoreNotTransitionFunction() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                internal sealed class TestAction: DslAction<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                    internal fun transition1() = transition<TestState.TestState1, TestState.TestState2>().transform { TestState.TestState2() }
                    internal fun transition2() = transition<TestState.TestState2, TestState.TestState1>().transform { TestState.TestState1() }
                    
                    private fun getInt() = 0
                    private fun getString() = "0"
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
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspCodeGeneratedSources()
        val generatedTestStateTransitionsFactory = kspGeneratedSources.first { it.path.endsWith("GeneratedTestFeatureTransitionsFactory.kt") }
        println(generatedTestStateTransitionsFactory.readText())
        Assertions.assertEquals(
            "import kotlin.Suppress\n" +
                "import kotlin.collections.List\n" +
                "import ru.kontur.mobile.visualfsm.Transition\n" +
                "import ru.kontur.mobile.visualfsm.TransitionsFactory\n" +
                "\n" +
                "internal class GeneratedTestFeatureTransitionsFactory : TransitionsFactory<TestState, TestAction> {\n" +
                "  @Suppress(\"REDUNDANT_ELSE_IN_WHEN\",\"UNCHECKED_CAST\")\n" +
                "  override fun create(action: TestAction): List<Transition<out TestState, out TestState>> =\n" +
                "      when (action) {\n" +
                "        is TestAction1 -> listOf(\n" +
                "            action.transition1().apply {\n" +
                "                _fromState = TestState.TestState1::class\n" +
                "                _toState = TestState.TestState2::class\n" +
                "            },\n" +
                "            action.transition2().apply {\n" +
                "                _fromState = TestState.TestState2::class\n" +
                "                _toState = TestState.TestState1::class\n" +
                "            },\n" +
                "        )\n" +
                "\n" +
                "        else -> error(\"Code generation error. Not all actions were processed in the when block.\")\n" +
                "    }\n" +
                "}\n",
            generatedTestStateTransitionsFactory.readText()
        )
    }
}