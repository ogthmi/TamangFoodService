package com.example.tamangfood.presentation.ui.mainapp.order.tracking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.util.concurrent.TimeUnit

object OsrmStreetRouteFetcher {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
    suspend fun fetchDrivingRoute(start: GeoPoint, end: GeoPoint): RouteAlongStreets? = withContext(Dispatchers.IO) {
        val url =
            "https://router.project-osrm.org/route/v1/driving/" +
                "${start.longitude},${start.latitude};${end.longitude},${end.latitude}" +
                "?overview=full&geometries=geojson"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "TamangFood-Android")
            .build()
        try {
            // Call API to get route from start to end
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                val routes = json.optJSONArray("routes") ?: return@withContext null
                if (routes.length() == 0) return@withContext null
                val route = routes.getJSONObject(0)
                val distanceMeters = route.getDouble("distance")
                val geometry = route.getJSONObject("geometry")
                val coords = geometry.getJSONArray("coordinates")
                val points = ArrayList<GeoPoint>(coords.length())
                for (i in 0 until coords.length()) {
                    val pair = coords.getJSONArray(i)
                    val lon = pair.getDouble(0)
                    val lat = pair.getDouble(1)
                    points.add(GeoPoint(lat, lon))
                }
                if (points.size < 2) return@withContext null
                RouteAlongStreets(points = points, distanceMeters = distanceMeters)
            }
        } catch (_: Exception) {
            null
        }
    }
}

data class RouteAlongStreets(
    val points: List<GeoPoint>,
    val distanceMeters: Double
)
