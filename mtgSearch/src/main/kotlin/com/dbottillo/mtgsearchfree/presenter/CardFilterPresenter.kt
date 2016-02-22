package com.dbottillo.mtgsearchfree.presenter

interface CardFilterPresenter {

    fun loadFilter()

    fun updateW(on: Boolean)

    fun updateU(on: Boolean)

    fun updateB(on: Boolean)

    fun updateR(on: Boolean)

    fun updateG(on: Boolean)

    fun updateArtifact(on: Boolean)

    fun updateLand(on: Boolean)

    fun updateEldrazi(on: Boolean)

    fun updateCommon(on: Boolean)

    fun updateUncommon(on: Boolean)

    fun updateRare(on: Boolean)

    fun updateMythic(on: Boolean)

}