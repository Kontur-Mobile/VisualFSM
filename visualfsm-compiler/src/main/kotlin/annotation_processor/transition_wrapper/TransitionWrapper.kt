package annotation_processor.transition_wrapper

import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlin.reflect.KClass

interface TransitionWrapper {
    val edgeName: String
    val fromState: KSClassDeclaration
    val toState: KSClassDeclaration
    val transitionClassSimpleName: String

    fun isClassOrSubclassOf(kClass: KClass<out Any>): Boolean
}