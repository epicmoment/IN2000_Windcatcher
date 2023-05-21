package no.met.in2000.windcatcher.planelogictests

import no.met.in2000.windcatcher.data.components.Plane
import no.met.in2000.windcatcher.data.repositories.PlaneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaneRepositoryDummy(plane: Plane): PlaneRepository {
    override val planeState: StateFlow<Plane> = MutableStateFlow(plane).asStateFlow()

    override fun update(newPlane: Plane){

    }
}