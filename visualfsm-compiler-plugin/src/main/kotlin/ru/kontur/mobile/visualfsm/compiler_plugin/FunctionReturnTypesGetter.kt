package ru.kontur.mobile.visualfsm.compiler_plugin

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.utils.nameOrSpecialName
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.references.symbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.isSubtypeOf
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid

class FunctionReturnTypesGetter(
    private val messageCollector: MessageCollector,
    private val firSession: FirSession,
) {

    fun get(declaration: FirFunction): Set<ConeKotlinType> {
        val body = declaration.body
        return if (body != null) {
            val visitor = FunctionBodyGetReturnTypesVisitor(declaration)
            body.accept(visitor)
            visitor.types.filter { type ->
                type.isSubtypeOf(declaration.returnTypeRef.coneType, firSession)
            }.toSet()
        } else {
            setOf(declaration.returnTypeRef.coneType)
        }.also {
            messageCollector.logWarning("Return types of function ${declaration.nameOrSpecialName.asString()}: $it")
        }
    }

    private inner class FunctionBodyGetReturnTypesVisitor(private val declaration: FirFunction) : FirVisitorVoid() {

        val types = mutableSetOf<ConeKotlinType>()

        // TODO Учесть сценарий с переопределением переменной
        val properties = mutableSetOf<FirProperty>()

        override fun visitElement(element: FirElement) {
            element.acceptChildren(this)
        }

        override fun visitProperty(property: FirProperty) {
            properties.add(property)
            property.acceptChildren(this)
        }

        // override fun visitVariableAssignment(variableAssignment: FirVariableAssignment) {
        //     val lhs = variableAssignment.lValue as? FirPropertyAccessExpression
        //     val symbol = lhs?.calleeReference?.symbol
        //     val property = properties.firstOrNull { it.symbol == symbol }
        //     if (property != null && property.isVar) {
        //         val rhsTypes = variableAssignment.rValue.getYieldTypes() // Define getYieldTypes like before
        //         // Union with existing from initializer
        //         messageCollector.logWarning("Adding re-assign types for ${property.name}: $rhsTypes")
        //         // Store in separate map<ConeSymbol, MutableSet<ConeKotlinType>>
        //     }
        //     super.visitVariableAssignment(variableAssignment)
        // }

        override fun visitReturnExpression(returnExpression: FirReturnExpression) {
            if (returnExpression.target.labeledElement != declaration) return
            returnExpression.result.accept(GetTypeVisitor(declaration, types, properties))
        }
    }

    private inner class GetTypeVisitor(
        private val declaration: FirFunction,
        private val types: MutableSet<ConeKotlinType>,
        private val properties: MutableSet<FirProperty>,
        private val prefix: String = "",
        private val parent: FirElement? = null,
    ) : FirVisitorVoid() {
        @OptIn(SymbolInternals::class)
        override fun visitElement(element: FirElement) {
            val childVisitor by lazy { GetTypeVisitor(declaration, types, properties, "$prefix    ", element) }
            when (element) {
                is FirReturnExpression -> {
                    if (element.target.labeledElement == declaration) {
                        element.result.accept(childVisitor)
                    }
                }

                is FirWhenExpression -> {
                    element.branches.forEach { it.accept(childVisitor) }
                }

                is FirWhenBranch -> {
                    element.result.accept(childVisitor)
                }

                is FirElvisExpression -> {
                    element.lhs.accept(childVisitor)
                    element.rhs.accept(childVisitor)
                }

                is FirBlock -> {
                    element.statements.forEachIndexed { index, statement ->
                        if (index == element.statements.lastIndex) {
                            statement.accept(childVisitor)
                        } else {
                            when (statement) {
                                is FirReturnExpression -> {
                                    statement.accept(childVisitor)
                                }

                                is FirProperty -> {
                                    properties.add(statement)
                                }
                            }
                        }
                    }
                }

                is FirFunctionCall -> {
                    messageCollector.logWarning("${prefix}FirFunctionCall expression (parent: ${parent}) ||| ${element.resolvedType}")
                    if (declaration.symbol == element.calleeReference.symbol) return
                    val firFunction = element.calleeReference.symbol?.fir as? FirFunction
                    if (firFunction != null) {
                        val typesOfFunction = FunctionReturnTypesGetter(messageCollector, firSession).get(firFunction)
                        messageCollector.logWarning("typesOfFunction: $typesOfFunction")
                        types.addAll(typesOfFunction)
                    } else {
                        types.add(element.resolvedType)
                    }
                }

                is FirLoop -> {
                    element.block.accept(childVisitor)
                }

                is FirTryExpression -> {
                    element.tryBlock.accept(childVisitor)
                    element.catches.forEach { catch -> catch.block.accept(childVisitor) }
                }

                is FirPropertyAccessExpression -> {
                    val property = properties.firstOrNull { it.symbol == element.calleeReference.symbol }
                    if (property != null) {
                        property.accept(childVisitor)
                    } else {
                        types.add(element.resolvedType)
                    }
                }

                else -> {
                    val hasFixExpressionChildrenInfoCollectorVisitor = HasFixExpressionChildrenInfoCollectorVisitor()
                    element.acceptChildren(hasFixExpressionChildrenInfoCollectorVisitor)
                    if (hasFixExpressionChildrenInfoCollectorVisitor.hasFirExpressionChild) {
                        messageCollector.logWarning("${prefix}expression with childrens() ${element}(fir = ${element is FirExpression})(parent: ${parent}) ||| ${(element as? FirExpression)?.resolvedType}")
                        element.acceptChildren(childVisitor)
                    } else {
                        if (element is FirExpression) {
                            messageCollector.logWarning("${prefix}expression without childrens ${element}(parent: ${parent}) ||| ${element.resolvedType}")
                            types.add(element.resolvedType)
                        }
                    }
                }
            }

        }
    }

    private inner class HasFixExpressionChildrenInfoCollectorVisitor() : FirVisitorVoid() {
        var hasFirExpressionChild = false
        override fun visitElement(element: FirElement) {
            if (element is FirExpression) {
                hasFirExpressionChild = true
            } else {
                element.acceptChildren(this)
            }
        }
    }
}