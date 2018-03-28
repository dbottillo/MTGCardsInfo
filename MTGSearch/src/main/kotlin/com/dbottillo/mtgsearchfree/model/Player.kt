package com.dbottillo.mtgsearchfree.model

data class Player(val id: Int,
                  var name: String,
                  var life: Int = 20,
                  var poisonCount: Int = 10,
                  var diceResult: Int = 0) {

    fun changeLife(value: Int) {
        this.life += value
    }

    fun changePoisonCount(value: Int) {
        this.poisonCount += value
    }

}
