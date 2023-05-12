package com.example.in2000_papirfly

import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.PolyLineWithThrowLocation
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowVievModelUtilities.drawHighScorePath
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowVievModelUtilities.drawPlanePath
import com.example.in2000_papirfly.ui.viewmodels.throwscreenlogic.ThrowVievModelUtilities.removeHighScorePath
import org.junit.Assert
import org.junit.Test
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline

class PlanePathTests {

    @Test
    fun drawHighScorePath_isCorrect() {
        val testOverlays = emptyList<Overlay>().toMutableList()
        val testLocation = "Test"
        val testPoint = GeoPoint(0.0, 0.0)

        drawHighScorePath(testOverlays, listOf(testPoint), testLocation)

        Assert.assertEquals(1, testOverlays.size)
        Assert.assertEquals(testLocation, (testOverlays[0] as PolyLineWithThrowLocation).throwLocation)
        Assert.assertEquals(testPoint, (testOverlays[0] as PolyLineWithThrowLocation).points[0])
    }

    @Test
    fun drawPlanePath_isCorrect() {
        val testOverlays = emptyList<Overlay>().toMutableList()
        val testOrigin = GeoPoint(0.0, 0.0)
        val testDestination = GeoPoint(1.0, 1.0)

        drawPlanePath(testOverlays, testOrigin, testDestination)

        Assert.assertEquals(1, testOverlays.size)
        Assert.assertEquals(testOrigin, (testOverlays[0] as Polyline).points[0])
        Assert.assertEquals(testDestination, (testOverlays[0] as Polyline).points[1])
    }

    @Test
    fun removeHighScorePath_isCorrect() {
        val testLocation1 = "Test1"
        val testLocation2 = "Test2"
        val overlayToBeRemoved = PolyLineWithThrowLocation(testLocation1)
        val testOverlays = listOf<Overlay>(
            overlayToBeRemoved,
            PolyLineWithThrowLocation(testLocation2),
            Polyline(),
        ).toMutableList()

        removeHighScorePath(testOverlays, testLocation1)

        Assert.assertEquals(2, testOverlays.size)
        Assert.assertEquals(false, testOverlays.contains(overlayToBeRemoved))
    }
}