package com.example.in2000_papirfly.planelogictests

import com.example.in2000_papirfly.data.components.Plane
import com.example.in2000_papirfly.data.repositories.PlaneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaneRepositoryDummy(plane: Plane): PlaneRepository {
    override val planeState: StateFlow<Plane> = MutableStateFlow(plane).asStateFlow()

    override fun update(newPlane: Plane){

    }
}