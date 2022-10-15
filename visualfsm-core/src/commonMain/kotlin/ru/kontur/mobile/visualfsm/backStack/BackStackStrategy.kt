package ru.kontur.mobile.visualfsm.backStack

/**
 * Back stack of states management strategy
 */
enum class BackStackStrategy {
    /**
     * Don't add to the back stack of states
     */
    NO_ADD,

    /**
     * Add destination state to the back stack of states
     */
    ADD,

    /**
     * Clear states back stack and add destination state to the back stack of states
     */
    NEW_ROOT
}