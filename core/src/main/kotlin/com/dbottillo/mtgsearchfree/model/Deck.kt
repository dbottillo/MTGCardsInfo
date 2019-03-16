package com.dbottillo.mtgsearchfree.model

data class Deck constructor(
    val id: Long,
    val name: String,
    val isArchived: Boolean,
    val numberOfCards: Int,
    val sizeOfSideboard: Int
)