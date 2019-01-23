package com.dbottillo.mtgsearchfree.repository

class CardPriceException(id: Int) : Throwable("impossible to fetch card price for $id")