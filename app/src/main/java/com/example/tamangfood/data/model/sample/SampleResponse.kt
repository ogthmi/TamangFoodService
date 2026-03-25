package com.example.tamangfood.data.model.sample

import com.example.tamangfood.domain.model.Sample

data class SampleResponse(
    val code: Int,
    val name: String,
)

fun SampleResponse.toDomain(): Sample {
    return Sample(
        name = this.name
    )
}