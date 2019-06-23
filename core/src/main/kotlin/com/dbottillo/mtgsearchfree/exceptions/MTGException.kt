package com.dbottillo.mtgsearchfree.exceptions

class MTGException(val code: ExceptionCode, message: String) : Exception(message)
