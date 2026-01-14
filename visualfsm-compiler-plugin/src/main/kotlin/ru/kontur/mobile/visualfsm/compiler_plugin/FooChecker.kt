package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirGetClassCall
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.types.resolvedType

class FooChecker(
    private val messageCollector: MessageCollector,
    session: FirSession,
) : FirAdditionalCheckersExtension(session) {

    override val declarationCheckers: DeclarationCheckers
        get() {
            val checker: FirSimpleFunctionChecker = object : FirSimpleFunctionChecker(MppCheckerKind.Common) {

                context(context: CheckerContext, reporter: DiagnosticReporter)
                override fun check(
                    declaration: FirSimpleFunction,
                ) {
                    val testAnnotation = declaration.annotations.firstOrNull { annotation ->
                        messageCollector.logWarning(
                            message = "Annotation name ${annotation.fqName(session)?.asString()}"
                        )
                        annotation.fqName(session)?.asString() == "ru.kontur.mobile.visualfsm.TransitionsFunction"
                    }
                    if (testAnnotation == null) return
                    messageCollector.logWarning(
                        "function ${declaration.name.identifier} return types: ${
                            FunctionReturnTypesGetter(
                                messageCollector,
                                session
                            ).get(declaration)
                        }"
                    )
                }

            }
            return object : DeclarationCheckers() {
                override val simpleFunctionCheckers = super.simpleFunctionCheckers + checker
            }
        }
}