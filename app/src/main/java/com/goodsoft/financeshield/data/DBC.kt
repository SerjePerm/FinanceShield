package com.goodsoft.financeshield.data

import android.provider.BaseColumns

object DBC {

    object Trans : BaseColumns {
        const val TABLE = "transactions"
        const val C_ID = "id"
        const val C_TITLE = "title"
        const val C_DATE = "date"
        const val C_CARD = "card"
        const val C_CATEGORY = "category"
        const val C_VALUE = "value"
    }

    object Categs : BaseColumns {
        const val TABLE = "categories"
        const val C_ID = "id"
        const val C_NAME = "name"
        const val C_ICON = "icon"
        const val C_EXPENSE = "expense"
        const val C_ACTIVE = "active"
    }

    object Cards : BaseColumns {
        const val TABLE = "cards"
        const val C_CARD = "card"
        const val C_VALUE = "value"
    }

    object Parsers : BaseColumns {
        const val TABLE = "parsers"
        const val C_ID = "id"
        const val C_TITLE = "title"
        const val C_PACKAGE = "package"
        const val C_KEYWORD = "keyword"
        const val C_IPAYEE = "iPayee"
        const val C_IVALUE = "iValue"
        const val C_ICARD = "iCard"
        const val C_IBALANCE = "iBalance"
        const val C_EXTITLE = "exTitle"
        const val C_EXTEXT = "exText"
        const val C_ACTIVE = "active"
    }

    object Links : BaseColumns {
        const val TABLE = "links"
        const val C_TITLE = "title"
        const val C_CATEGORY = "category"
    }

    object Notifs : BaseColumns {
        const val TABLE = "notifications"
        const val C_ID = "id"
        const val C_PACKAGE = "package"
        const val C_TITLE = "title"
        const val C_TEXT = "text"
        const val C_DATE = "date"
    }

}