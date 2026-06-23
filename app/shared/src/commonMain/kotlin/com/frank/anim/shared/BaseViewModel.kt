package com.frank.anim.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * ViewModel基类，提供通用的viewModelScope
 */
abstract class BaseViewModel {
    /**
     * 便捷的协程作用域访问
     */
    val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob())

    /**
     * 清理资源
     */
    open fun onCleared() {
        viewModelScope.cancel()
    }
}