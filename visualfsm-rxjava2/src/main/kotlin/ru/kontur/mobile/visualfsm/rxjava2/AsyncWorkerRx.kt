package ru.kontur.mobile.visualfsm.rxjava2

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.kontur.mobile.visualfsm.Action
import ru.kontur.mobile.visualfsm.State


/**
 * Manages the start and stop of state-based asynchronous tasks
 */
abstract class AsyncWorkerRx<STATE : State, ACTION : Action<STATE>> {
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
     * @param fromState the state from which the asynchronous task was started [State]
     * @param action launched [Action]
     */
    fun proceed(fromState: STATE, action: ACTION) {
        // If the current state does not match the state from which the task started, the result of its task is no longer expected
        if (fromState == feature?.getCurrentState()) {
            feature?.proceed(action) ?: throw IllegalStateException("Feature is unbound")
        }
    }

    /**
     * Submits an [action][Action] to be executed in the [feature][FeatureRx]
     *
     * @param action launched [Action]
     */
    @Deprecated(message = "Use the new proceed(fromState, action) method", level = DeprecationLevel.ERROR)
    fun proceed(action: ACTION) {
        feature?.proceed(action) ?: throw IllegalStateException("Feature is unbound")
    }

    /**
     * Handle new task
     */
    private fun handleTask(task: AsyncWorkerTaskRx<STATE>) {
        when (task) {
            is AsyncWorkerTaskRx.Cancel -> dispose()
            is AsyncWorkerTaskRx.ExecuteAndCancelExist -> {
                disposeAndLaunch(task.state, task.func)
            }
            is AsyncWorkerTaskRx.ExecuteIfNotExist -> {
                if (launchedAsyncStateDisposable?.isDisposed != false || task.state != launchedAsyncState) {
                    disposeAndLaunch(task.state, task.func)
                }
            }
        }
    }

    /**
     * Dispose current task and launch new
     */
    @Synchronized
    private fun disposeAndLaunch(stateToLaunch: STATE, func: () -> Disposable) {
        launchedAsyncState = stateToLaunch
        launchedAsyncStateDisposable?.dispose()
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