package com.dbottillo.mtgsearchfree.database

import com.dbottillo.mtgsearchfree.model.CMCParam
import com.dbottillo.mtgsearchfree.model.PTParam

fun StringBuilder.appendCast(name: String) {
    this.append("CAST(")
    this.append(name)
    this.append(" as integer) ")
}

internal class QueryComposer(initial: String) {

    private val stringBuilder: StringBuilder = StringBuilder(initial) // NOPMD
    private val selection = mutableListOf<String>()

    internal class Output(var query: String, var selection: List<String>)

    fun addCMCParam(cmcParam: CMCParam?) {
        cmcParam?.let {
            checkFirstParam()
            if (it.operator == "=") {
                if ((it.stringValues.size == 1 && it.stringValues[0].toIntOrNull() == it.numericValue) || it.stringValues.isEmpty()) {
                    stringBuilder.append("cmc").append(it.operator).append("?")
                    addSelection(it.operator, it.numericValue.toString())
                } else {
                    stringBuilder.append("manaCost").append(" ").append(it.operator).append(" ?")
                    addSelection(it.operator, it.stringValues.flatMap { it.toList() }.fold("") { total, next -> "$total{$next}" })
                }
            } else {
                stringBuilder.append("cmc").append(it.operator).append("?")
                addSelection(it.operator, it.numericValue.toString())
                it.stringValues.forEach { stringValue ->
                    if (stringValue.toIntOrNull() == null || it.stringValues.contains("X")) {
                        stringBuilder.append(" AND manaCost LIKE ?")
                        addSelection(LIKE_OPERATOR, stringValue.fold("") { total, next -> "$total{$next}" })
                    }
                }
            }
        }
    }

    fun addPTParam(name: String, ptParam: PTParam?) {
        ptParam?.let {
            if (it.value == -1) {
                checkFirstParam()
                stringBuilder.append(name).append(" ").append(LIKE_OPERATOR).append(" ?")
                addSelection(it.operator, "%*%")
            } else {
                if (isValid(name, ptParam.operator, ptParam.value.toString())) {
                    checkFirstParam()
                    stringBuilder.append("(")
                    stringBuilder.appendCast(name)
                    stringBuilder.append(if (ptParam.operator == "IS") "=" else ptParam.operator)
                    stringBuilder.append(" ? AND ")
                    stringBuilder.append(name)
                    stringBuilder.append(" GLOB '[0-9]')")
                    addSelection(ptParam.operator, ptParam.value.toString())
                }
            }
        }
    }

    fun addLikeParam(name: String, value: String) {
        addParam(name, LIKE_OPERATOR, value)
    }

    fun addIsNullParam(name: String) {
        checkFirstParam()
        stringBuilder.append(name)
        stringBuilder.append(" IS ''")
    }

    fun addParam(name: String, operator: String, value: Int) {
        if (isValid(name, operator, value.toString())) {
            checkFirstParam()
            stringBuilder.append("(")
            stringBuilder.appendCast(name)
            stringBuilder.append(operator)
            stringBuilder.append(" ? AND ")
            stringBuilder.append(name)
            stringBuilder.append(" != '')")
            addSelection(operator, value.toString())
        }
    }

    fun addParam(name: String?, operator: String, value: String) {
        if (isValid(name, operator, value)) {
            checkFirstParam()
            addOneParam(name!!, operator)
            addSelection(operator, value)
        }
    }

    fun addMultipleParam(name: String?, operator: String, paramOperator: String, vararg values: String) {
        if (name == null || name.isEmpty() || values.isEmpty()) {
            return
        }
        checkFirstParam()
        stringBuilder.append("(")
        for (i in values.indices) {
            addOneParam(name, operator)
            addSelection(operator, values[i])
            if (i < values.size - 1) {
                stringBuilder.append(" ")
                stringBuilder.append(paramOperator)
                stringBuilder.append(" ")
            }
        }
        stringBuilder.append(")")
    }

    fun append(path: String) {
        stringBuilder.append(" ")
        stringBuilder.append(path)
    }

    private fun addSelection(operator: String, value: String) {
        if (operator.equals(LIKE_OPERATOR, ignoreCase = true)) {
            selection.add("%$value%")
        } else {
            selection.add(value)
        }
    }

    private fun checkFirstParam() {
        if (selection.isNotEmpty()) {
            stringBuilder.append(" AND ")
        } else {
            stringBuilder.append(" WHERE ")
        }
    }

    private fun addOneParam(name: String, operator: String) {
        stringBuilder.append(name)
                .append(" ")
                .append(operator).append(" ?")
    }

    private fun isValid(name: String?, operator: String?, value: String): Boolean {
        return !(name == null || name.isEmpty() || operator == null || operator.isEmpty() || value.isEmpty())
    }

    fun build(): Output {
        return Output(stringBuilder.toString(), selection)
    }
}

private const val LIKE_OPERATOR = "LIKE"
private const val IS_OPERATOR = "IS"
