package com.goodsoft.financeshield.utils

fun boolToInt(bool: Boolean): Int = if (bool) 1 else 0

fun floatToFormattedString(value: Float): String {
    var result = value.toString()
    if (value>999.99 || value<-999.99) { //add space to thousands
        val str = value.toString()
        val index = str.indexOf(".")-3
        result = str.substring(0,index) + " " + str.substring(index)
    }
    if (result[result.length-1].toString() == "0") { result = result.substring(0,result.length-2) } //remove ".0"
    return result
}

