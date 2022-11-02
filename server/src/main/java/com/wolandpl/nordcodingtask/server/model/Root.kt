package com.wolandpl.nordcodingtask.server.model

data class Root(
    val start: String,
    val services: List<Service>
)

data class Service(
    val name: String,
    val url: String
)
