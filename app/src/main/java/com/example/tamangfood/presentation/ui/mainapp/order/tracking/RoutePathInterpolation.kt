package com.example.tamangfood.presentation.ui.mainapp.order.tracking

import org.osmdroid.util.GeoPoint

/**
 * Point on [points] at [progress] in 0f..1f, measured by distance along the polyline.
 */
fun positionAlongRoute(points: List<GeoPoint>, progress: Float): GeoPoint {
    if (points.isEmpty()) return GeoPoint(0.0, 0.0)
    if (points.size == 1) return points[0]
    val p = progress.coerceIn(0f, 1f).toDouble()
    var total = 0.0
    val segmentLens = DoubleArray(points.size - 1)
    for (i in 0 until points.size - 1) {
        val len = points[i].distanceToAsDouble(points[i + 1])
        segmentLens[i] = len
        total += len
    }
    if (total <= 0.0) return points.last()
    var remaining = total * p
    var i = 0
    while (i < segmentLens.size) {
        val segLen = segmentLens[i]
        if (remaining <= segLen) {
            val ratio = if (segLen > 0) remaining / segLen else 0.0
            val a = points[i]
            val b = points[i + 1]
            val lat = a.latitude + (b.latitude - a.latitude) * ratio
            val lon = a.longitude + (b.longitude - a.longitude) * ratio
            return GeoPoint(lat, lon)
        }
        remaining -= segLen
        i++
    }
    return points.last()
}
