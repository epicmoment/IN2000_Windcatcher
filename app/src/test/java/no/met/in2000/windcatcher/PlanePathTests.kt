package no.met.in2000.windcatcher

import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.PolyLineWithThrowLocation
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.drawHighScorePath
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.drawPlanePath
import no.met.in2000.windcatcher.ui.viewmodels.throwscreenlogic.ThrowScreenUtilities.removeHighScorePath
import org.junit.Assert.assertEquals
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

        assertEquals(1, testOverlays.size)
        assertEquals(testLocation, (testOverlays[0] as PolyLineWithThrowLocation).throwLocation)
        assertEquals(testPoint, (testOverlays[0] as PolyLineWithThrowLocation).points[0])
    }

    @Test
    fun drawPlanePath_isCorrect() {
        val testOverlays = emptyList<Overlay>().toMutableList()
        val testOrigin = GeoPoint(0.0, 0.0)
        val testDestination = GeoPoint(1.0, 1.0)

        drawPlanePath(testOverlays, testOrigin, testDestination)

        assertEquals(1, testOverlays.size)
        assertEquals(testOrigin, (testOverlays[0] as Polyline).points[0])
        assertEquals(testDestination, (testOverlays[0] as Polyline).points[1])
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

        assertEquals(2, testOverlays.size)
        assertEquals(false, testOverlays.contains(overlayToBeRemoved))
    }
}