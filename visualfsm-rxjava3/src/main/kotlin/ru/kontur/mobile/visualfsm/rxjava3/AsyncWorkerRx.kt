package ru.kontur.mobile.visualfsm.rxjava3

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.Feature
import ru.kontur.mobile.visualfsm.State

/**
 * Manages the start and stop of state-based asynchronous tasks
 */
abstract class AsyncWorkerRx<STATE : State, ACTION : Action<STATE>> {
    @Volatile
    private var feature: FeatureRx<STATE, ACTION>? = null
    private var launchedAsyncState: STATE? = null
    private var subscriptionDisposable: Disposable? = null
    private var launchedAsyncStateDisposable: Disposable? = null

    /**
     * [The scheduler][Scheduler] for task management (start and stop tasks)
     */
    protected open val taskManagementScheduler: Scheduler = Schedulers.computation()

    /**
     * Binds received [feature][FeatureRx] to [async worker][AsyncWorkerRx]
     * and starts [observing][FeatureRx.observeState] [states][State]
     *
     * @param feature provided [FeatureRx]
     */
    internal fun bind(feature: FeatureRx<STATE, ACTION>) {
        this.feature = feature
        subscriptionDisposable = feature.observeState()
            .observeOn(taskManagementScheduler)
            .map(::onNextState)
            .subscribe(::handleTask, ::onStateSubscriptionError)
    }

    /**
     * Dispose current task and unbind feature. Use it if the async worker is no longer needed (onCleared)
     * If you only need to stop the current task, use feature.proceed(_SomeActionForStop_())
     */
    fun unbind() {
        dispose()
        subscriptionDisposable?.dispose()
        feature = null
    }

    /**
     * Provides a state to manage async work
     * Don't forget to handle each task's errors in this method,
     * if an unhandled exception occurs, then fsm may stuck in the current state
     * and the onStateSubscriptionError method will be called
     *
     * @param state a next [state][State]
     * @return [AsyncWorkerTaskRx] for async work handling
     */
    protected abstract fun onNextState(state: STATE): AsyncWorkerTaskRx<STATE>

    /**
     * Called when catched subscription error
     * Override this for logs or metrics
     * Call of this method signals the presence of unhandled exceptions in the [onNextState] method.
     * @param throwable catched [Throwable]
     */
    protected open fun onStateSubscriptionError(throwable: Throwable) {
        throw throwable
    }

    /**
     * Submits an [action][Action] to be executed in the [feature][FeatureRx]
     *
     * @param action launched [Action]
     */
    fun AsyncWorkerTaskRx.ExecuteIfNotExist<STATE>.proceed(action: ACTION) {
        proceed(this.state, action, false)
    }

    /**
     * Submits an [action][Action] to be executed in the [feature][FeatureRx]
     *
     * @param action launched [Action]
     */
    fun AsyncWorkerTaskRx.ExecuteAndCancelExist<STATE>.proceed(action: ACTION) {
        proceed(this.state, action, false)
    }

    /**
     * Submits an [action][Action] to be executed in the [feature][FeatureRx]
     *
     * @param action launched [Action]
     */
    fun AsyncWorkerTaskRx.ExecuteIfNotExistWithSameClass<STATE>.proceed(action: ACTION) {
        proceed(this.state, action, true)
    }

    /**
     * Submits an [action][Action] to be executed in the [feature][Feature]
     *
     * @param fromState the state from which the asynchronous task was started [State]
     * @param action launched [Action]
     * @param handleActionForSameStateClass handle action for same state class
     */
    private fun proceed(fromState: STATE, action: ACTION, handleActionForSameStateClass: Boolean) {
        val feature = feature ?: error("Feature is unbound")
        synchronized(feature) {
            // If the current state does not match the state from which the task started, the result of its task is no longer expected
            if ((handleActionForSameStateClass && fromState::class == feature.getCurrentState()::class)
                || fromState == feature.getCurrentState()
            ) {
                feature.proceed(action)
            }
        }
    }

    /**
     * Handle new task
     */
    private fun handleTask(task: AsyncWorkerTaskRx<STATE>) {
        when (task) {
            is AsyncWorkerTaskRx.Cancel -> dispose()
            is AsyncWorkerTaskRx.ExecuteAndCancelExist -> {
                disposeAndLaunch(task.state) { task.func(task) }
            }

            is AsyncWorkerTaskRx.ExecuteIfNotExist -> {
                if (launchedAsyncStateDisposable?.isDisposed != false || task.state != launchedAsyncState) {
                    disposeAndLaunch(task.state) { task.func(task) }
                }
            }

            is AsyncWorkerTaskRx.ExecuteIfNotExistWithSameClass -> {
                val launchedState = launchedAsyncState
                if (launchedState == null || task.state::class != launchedState::class || launchedAsyncStateDisposable?.isDisposed != false) {
                    disposeAndLaunch(task.state) { task.func(task) }
                }
            }
        }
    }

    /**
     * Dispose current task and launch new
     */
    @Synchronized
    private fun disposeAndLaunch(stateToLaunch: STATE, func: () -> Disposable) {
        launchedAsyncStateDisposable?.dispose()
        launchedAsyncState = stateToLaunch
        launchedAsyncStateDisposable = func()
    }

    /**
     * Disposes async task
     */
    @Synchronized
    private fun dispose() {
        launchedAsyncStateDisposable?.dispose()
        launchedAsyncState = null
    }
}