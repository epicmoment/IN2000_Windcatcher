package no.met.in2000.windcatcher.data.repositories

import no.met.in2000.windcatcher.data.components.Plane
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


interface PlaneRepository {
    val planeState: StateFlow<Plane>
    fun update(newPlane: Plane)
}
class PlaneRepo: PlaneRepository {

    private val _planeState = MutableStateFlow(Plane())
    override val planeState : StateFlow<Plane> = _planeState.asStateFlow()

    override fun update(newPlane : Plane){
        _planeState.update { newPlane }
    }
}