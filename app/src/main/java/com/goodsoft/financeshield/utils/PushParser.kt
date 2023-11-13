package com.goodsoft.financeshield.utils

fun splitStrToArray(text: String, title: String): Array<String> {
    var resultArr: Array<String> = arrayOf()
    resultArr += title
    var word = ""
    for (i in text.indices) {
        if ((i > 0) && (i < text.indices.last)) if ((text[i].code == 32) && isNumber(text[i - 1]) && isNumber(text[i + 1])) continue // space - 32; remove space when "1 234" -> "1234"
        if ((text[i].code == 10) || (text[i].code == 32)) {
            resultArr += word; word = ""
        } else word += text[i] // \n - 010; space - 32
    }
    resultArr += word
    return resultArr
}

fun clearNumber(number: String): String {
    if (number == "ошибка") return number
    var result = ""
    var firstDot = false
    for (i in number.indices) {
        if ((number[i].code >= 48) && (number[i].code <= 57)) result += number[i] // 0-9 - 48 - 57
        if ((number[i].code == 44) || (number[i].code == 46)) { // "," - 44; "." - 46
            if (firstDot == true) continue //if first dot is already found -> break
            if ((i > 0) && (i < number.indices.last)) if ((isNumber(number[i - 1])) && (isNumber(number[i + 1]))) {
                firstDot = true //first dot founded
                result += "." //add dot to result
            }
        }
    }
    return result
}

fun isNumber(letter: Char): Boolean {
    return (letter.code >= 48) and (letter.code <= 58)
}