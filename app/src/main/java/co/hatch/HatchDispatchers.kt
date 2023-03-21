package co.hatch

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

// TODO make this an interface
class HatchDispatchers {
    val Default: CoroutineDispatcher = Dispatchers.Default
    val Main: MainCoroutineDispatcher = Dispatchers.Main
    val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    val IO: CoroutineDispatcher = Dispatchers.IO
}