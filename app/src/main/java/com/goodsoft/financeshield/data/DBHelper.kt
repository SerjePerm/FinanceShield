package com.goodsoft.financeshield.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.goodsoft.financeshield.utils.dateToSQLite
import com.goodsoft.financeshield.utils.extractDateTimeFromSQLite
import java.text.SimpleDateFormat
import java.util.*

//---------------------------- Transactions -------------------------------------
private const val SQL_CREATE_TRANSACTIONS = "CREATE TABLE ${DBC.Trans.TABLE} (" +
        "${DBC.Trans.C_ID} INTEGER PRIMARY KEY," +
        "${DBC.Trans.C_TITLE} TEXT NOT NULL," +
        "${DBC.Trans.C_DATE} TEXT," +
        "${DBC.Trans.C_CARD} INTEGER DEFAULT 0," +
        "${DBC.Trans.C_CATEGORY} TEXT," +
        "${DBC.Trans.C_VALUE} REAL DEFAULT 0);"
private const val SQL_DROP_TRANSACTIONS = "DROP TABLE IF EXISTS ${DBC.Trans.TABLE}"

//---------------------------- Categories ---------------------------------------
private const val SQL_CREATE_CATEGORIES = "CREATE TABLE ${DBC.Categs.TABLE} (" +
        "${DBC.Categs.C_ID} INTEGER PRIMARY KEY," +
        "${DBC.Categs.C_NAME} TEXT NOT NULL," +
        "${DBC.Categs.C_ICON} INTEGER," +
        "${DBC.Categs.C_EXPENSE} INTEGER," +
        "${DBC.Categs.C_ACTIVE} INTEGER);"
private const val SQL_DROP_CATEGORIES = "DROP TABLE IF EXISTS ${DBC.Categs.TABLE}"

//---------------------------- Cards --------------------------------------------
private const val SQL_CREATE_CARDS = "CREATE TABLE ${DBC.Cards.TABLE} (" +
        "${DBC.Cards.C_CARD} INTEGER PRIMARY KEY," +
        "${DBC.Cards.C_VALUE} REAL);"
private const val SQL_DROP_CARDS = "DROP TABLE IF EXISTS ${DBC.Cards.TABLE}"

//---------------------------- Parsers ------------------------------------------
private const val SQL_CREATE_PARSERS = "CREATE TABLE ${DBC.Parsers.TABLE} (" +
        "${DBC.Parsers.C_ID} INTEGER PRIMARY KEY," +
        "${DBC.Parsers.C_TITLE} TEXT NOT NULL," +
        "${DBC.Parsers.C_PACKAGE} TEXT," +
        "${DBC.Parsers.C_KEYWORD} TEXT," +
        "${DBC.Parsers.C_IPAYEE} INTEGER," +
        "${DBC.Parsers.C_IVALUE} INTEGER," +
        "${DBC.Parsers.C_ICARD} INTEGER," +
        "${DBC.Parsers.C_IBALANCE} INTEGER," +
        "${DBC.Parsers.C_EXTITLE} TEXT," +
        "${DBC.Parsers.C_EXTEXT} TEXT," +
        "${DBC.Parsers.C_ACTIVE} INTEGER);"
private const val SQL_DROP_PARSERS = "DROP TABLE IF EXISTS ${DBC.Parsers.TABLE}"

//---------------------------- Links --------------------------------------------
private const val SQL_CREATE_LINKS = "CREATE TABLE ${DBC.Links.TABLE} (" +
        "${DBC.Links.C_TITLE} TEXT PRIMARY KEY," +
        "${DBC.Links.C_CATEGORY} TEXT);"
private const val SQL_DROP_LINKS = "DROP TABLE IF EXISTS ${DBC.Links.TABLE}"

//---------------------------- Notifications-------------------------------------
private const val SQL_CREATE_NOTIFICATIONS = "CREATE TABLE ${DBC.Notifs.TABLE} (" +
        "${DBC.Notifs.C_ID} INTEGER PRIMARY KEY," +
        "${DBC.Notifs.C_PACKAGE} TEXT NOT NULL," +
        "${DBC.Notifs.C_TITLE} TEXT NOT NULL," +
        "${DBC.Notifs.C_TEXT} TEXT NOT NULL," +
        "${DBC.Notifs.C_DATE} TEXT);"
private const val SQL_DROP_NOTIFICATIONS = "DROP TABLE IF EXISTS ${DBC.Notifs.TABLE}"
//------------------------------------------------------------------------------

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FinanceShieldDB"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TRANSACTIONS)
        db?.execSQL(SQL_CREATE_CATEGORIES)
        db?.execSQL(SQL_CREATE_CARDS)
        db?.execSQL(SQL_CREATE_PARSERS)
        db?.execSQL(SQL_CREATE_LINKS)
        db?.execSQL(SQL_CREATE_NOTIFICATIONS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DROP_TRANSACTIONS)
        db?.execSQL(SQL_DROP_CATEGORIES)
        db?.execSQL(SQL_DROP_CARDS)
        db?.execSQL(SQL_DROP_PARSERS)
        db?.execSQL(SQL_DROP_LINKS)
        db?.execSQL(SQL_DROP_NOTIFICATIONS)
        onCreate(db)
    }

    //---------------------------- Transactions -------------------------------------
    fun addTransaction(transactionParam: Transaction) {
        val values = ContentValues()
        values.put(DBC.Trans.C_TITLE, transactionParam.title)
        values.put(DBC.Trans.C_DATE, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT)).format(transactionParam.date))
        values.put(DBC.Trans.C_CARD, transactionParam.card)
        values.put(DBC.Trans.C_CATEGORY, transactionParam.category)
        values.put(DBC.Trans.C_VALUE, transactionParam.value)
        val db = this.writableDatabase
        db.insert(DBC.Trans.TABLE, null, values)
        db.close()
    }

    fun getTransactions(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM ${DBC.Trans.TABLE} ORDER BY datetime(${DBC.Trans.C_DATE}) DESC LIMIT 1000", null)
    }

    @SuppressLint("Range")
    fun getTransaction(id: Int): Transaction {
        val query = "SELECT * FROM ${DBC.Trans.TABLE} WHERE ${DBC.Trans.C_ID} = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        cursor.moveToFirst()
        //
        val tmpTitle = cursor.getString(cursor.getColumnIndex(DBC.Trans.C_TITLE))
        val tmpDateStr = cursor.getString(cursor.getColumnIndex(DBC.Trans.C_DATE))
        val tmpDate =  extractDateTimeFromSQLite(tmpDateStr)
        //val tmpDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT)).parse(cursor.getString(cursor.getColumnIndex(DBC.Trans.C_DATE)))
        val tmpCard = cursor.getInt(cursor.getColumnIndex(DBC.Trans.C_CARD))
        val tmpCategory = cursor.getString(cursor.getColumnIndex(DBC.Trans.C_CATEGORY))
        val tmpValue = cursor.getFloat(cursor.getColumnIndex(DBC.Trans.C_VALUE))
        //
        cursor.close()
        return Transaction(id, tmpTitle, tmpDate!!, tmpCard, tmpCategory, tmpValue)
    }

    fun updateTransaction(transactionParam: Transaction) {
        val whereClause = DBC.Trans.C_ID + " = ?"
        val values = ContentValues()
        //
        values.put(DBC.Trans.C_TITLE, transactionParam.title)
        values.put(DBC.Trans.C_DATE, dateToSQLite(transactionParam.date))
        values.put(DBC.Trans.C_CARD, transactionParam.card)
        values.put(DBC.Trans.C_CATEGORY, transactionParam.category)
        values.put(DBC.Trans.C_VALUE, transactionParam.value)
        //
        val db = this.writableDatabase
        val recordsUpdated = db.update(DBC.Trans.TABLE, values, whereClause, arrayOf(transactionParam.id.toString()))
        Log.i(this.javaClass.name, "Updated $recordsUpdated records")
    }

    fun delTransaction(id: Int) {
        val whereClause = DBC.Trans.C_ID + " = ?"
        val db = this.writableDatabase
        db.delete(DBC.Trans.TABLE, whereClause, arrayOf(id.toString()))
    }

    //---------------------------- Categories ---------------------------------------
    fun addCategory(categoryParam: Category) {
        val values = ContentValues()
        values.put(DBC.Categs.C_NAME, categoryParam.name)
        values.put(DBC.Categs.C_ICON, categoryParam.icon)
        values.put(DBC.Categs.C_EXPENSE, categoryParam.expense)
        values.put(DBC.Categs.C_ACTIVE, categoryParam.active)
        val db = this.writableDatabase
        db.insert(DBC.Categs.TABLE, null, values)
        db.close()
    }

    fun getCategories(onlyActive: Boolean): Cursor? {
        val db = this.readableDatabase
        return if (onlyActive) db.rawQuery("SELECT * FROM ${DBC.Categs.TABLE} WHERE ${DBC.Categs.C_ACTIVE} = 1 ORDER BY ${DBC.Categs.C_NAME} ASC", null)
        else db.rawQuery("SELECT * FROM ${DBC.Categs.TABLE} ORDER BY ${DBC.Categs.C_ACTIVE} DESC, ${DBC.Categs.C_NAME} ASC", null)
    }

    @SuppressLint("Range")
    fun getCategory(id: Int): Category {
        val query = "SELECT * FROM ${DBC.Categs.TABLE} WHERE ${DBC.Categs.C_ID} = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        cursor.moveToFirst()
        //
        val tmpName = cursor.getString(cursor.getColumnIndex(DBC.Categs.C_NAME))
        val tmpIcon = cursor.getInt(cursor.getColumnIndex(DBC.Categs.C_ICON))
        val tmpExpense = cursor.getInt(cursor.getColumnIndex(DBC.Categs.C_EXPENSE))
        val tmpActive = cursor.getInt(cursor.getColumnIndex(DBC.Categs.C_ACTIVE))
        //
        cursor.close()
        return Category(id, tmpName, tmpIcon, tmpExpense, tmpActive)
    }

    fun updateCategory(categoryParam: Category) {
        val whereClause = DBC.Categs.C_ID + " = ?"
        val values = ContentValues()
        //
        values.put(DBC.Categs.C_NAME, categoryParam.name)
        values.put(DBC.Categs.C_ICON, categoryParam.icon)
        values.put(DBC.Categs.C_EXPENSE, categoryParam.expense)
        values.put(DBC.Categs.C_ACTIVE, categoryParam.active)
        //
        val db = this.writableDatabase
        val recordsUpdated = db.update(DBC.Categs.TABLE, values, whereClause, arrayOf(categoryParam.id.toString()))
        Log.i(this.javaClass.name, "Updated $recordsUpdated records")
    }

    fun delCategory(id: Int) {
        val whereClause = DBC.Categs.C_ID + " = ?"
        val db = this.writableDatabase
        db.delete(DBC.Categs.TABLE, whereClause, arrayOf(id.toString()))
    }

    fun changeCategoryActive(id: Int, setToOn: Boolean) {
        val whereClause = DBC.Categs.C_ID + " = ?"
        val values = ContentValues()
        //
        if (setToOn) values.put(DBC.Categs.C_ACTIVE, 1)
        else values.put(DBC.Categs.C_ACTIVE, 0)
        //
        val db = this.writableDatabase
        val recordsUpdated = db.update(DBC.Categs.TABLE, values, whereClause, arrayOf(id.toString()))
        Log.i(this.javaClass.name, "Updated $recordsUpdated records")
    }

    fun getCategoryIcon(categoryName: String): Int {
        val query = "SELECT ${DBC.Categs.C_ICON} FROM ${DBC.Categs.TABLE} WHERE ${DBC.Categs.C_NAME} = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(categoryName))
        var result = 0
        if (cursor.moveToFirst()) result = cursor.getInt(0)
        cursor.close()
        return result
    }

    //---------------------------- Cards --------------------------------------------
    fun addOrUpdateCard(card: Int, value: Float) {
        val db = this.writableDatabase
        db.execSQL("INSERT INTO ${DBC.Cards.TABLE} VALUES($card, $value) ON CONFLICT(${DBC.Cards.C_CARD}) DO UPDATE SET ${DBC.Cards.C_VALUE}=$value")
        db.close()
    }

    fun getCards(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM ${DBC.Cards.TABLE} ORDER BY ${DBC.Cards.C_CARD}", null)
    }

    fun delCard(card: Int) {
        val whereClause = DBC.Cards.C_CARD + " = ?"
        val db = this.writableDatabase
        db.delete(DBC.Cards.TABLE, whereClause, arrayOf(card.toString()))
    }

    //---------------------------- Parsers ------------------------------------------
    fun addParser(parserParam: Parser) {
        val values = ContentValues()
        values.put(DBC.Parsers.C_TITLE, parserParam.title)
        values.put(DBC.Parsers.C_PACKAGE, parserParam.packag)
        values.put(DBC.Parsers.C_KEYWORD, parserParam.keyword)
        values.put(DBC.Parsers.C_IPAYEE, parserParam.ipayee)
        values.put(DBC.Parsers.C_IVALUE, parserParam.ivalue)
        values.put(DBC.Parsers.C_ICARD, parserParam.icard)
        values.put(DBC.Parsers.C_IBALANCE, parserParam.ibalance)
        values.put(DBC.Parsers.C_EXTITLE, parserParam.extitle)
        values.put(DBC.Parsers.C_EXTEXT, parserParam.extext)
        values.put(DBC.Parsers.C_ACTIVE, parserParam.active)
        //
        val db = this.writableDatabase
        db.insert(DBC.Parsers.TABLE, null, values)
        db.close()
    }

    fun getParsers(onlyActive: Boolean): Cursor? {
        val db = this.readableDatabase
        return if (onlyActive) db.rawQuery("SELECT * FROM ${DBC.Parsers.TABLE} WHERE ${DBC.Parsers.C_ACTIVE} = 1 ORDER BY ${DBC.Parsers.C_TITLE} DESC", null)
        else db.rawQuery("SELECT * FROM ${DBC.Parsers.TABLE} ORDER BY ${DBC.Parsers.C_TITLE} DESC", null)
    }

    @SuppressLint("Range")
    fun getParser(id: Int): Parser {
        val query = "SELECT * FROM ${DBC.Parsers.TABLE} WHERE ${DBC.Parsers.C_ID} = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        cursor.moveToFirst()
        //
        val tmpTitle = cursor.getString(cursor.getColumnIndex(DBC.Parsers.C_TITLE))
        val tmpPackage = cursor.getString(cursor.getColumnIndex(DBC.Parsers.C_PACKAGE))
        val tmpKeyword = cursor.getString(cursor.getColumnIndex(DBC.Parsers.C_KEYWORD))
        val tmpIPayee = cursor.getInt(cursor.getColumnIndex(DBC.Parsers.C_IPAYEE))
        val tmpIValue = cursor.getInt(cursor.getColumnIndex(DBC.Parsers.C_IVALUE))
        val tmpICard = cursor.getInt(cursor.getColumnIndex(DBC.Parsers.C_ICARD))
        val tmpIBalance = cursor.getInt(cursor.getColumnIndex(DBC.Parsers.C_IBALANCE))
        val tmpExTitle = cursor.getString(cursor.getColumnIndex(DBC.Parsers.C_EXTITLE))
        val tmpExText = cursor.getString(cursor.getColumnIndex(DBC.Parsers.C_EXTEXT))
        val tmpActive = cursor.getInt(cursor.getColumnIndex(DBC.Parsers.C_ACTIVE))
        //
        cursor.close()
        return Parser(id, tmpTitle, tmpPackage, tmpKeyword, tmpIPayee, tmpIValue, tmpICard, tmpIBalance, tmpExTitle, tmpExText, tmpActive)
    }

    fun updateParser(parserParam: Parser) {
        val whereClause = DBC.Parsers.C_ID + " = ?"
        val values = ContentValues()
        //
        values.put(DBC.Parsers.C_TITLE, parserParam.title)
        values.put(DBC.Parsers.C_PACKAGE, parserParam.packag)
        values.put(DBC.Parsers.C_KEYWORD, parserParam.keyword)
        values.put(DBC.Parsers.C_IPAYEE, parserParam.ipayee)
        values.put(DBC.Parsers.C_IVALUE, parserParam.ivalue)
        values.put(DBC.Parsers.C_ICARD, parserParam.icard)
        values.put(DBC.Parsers.C_IBALANCE, parserParam.ibalance)
        values.put(DBC.Parsers.C_EXTITLE, parserParam.extitle)
        values.put(DBC.Parsers.C_EXTEXT, parserParam.extext)
        values.put(DBC.Parsers.C_ACTIVE, parserParam.active)
        //
        val db = this.writableDatabase
        val recordsUpdated = db.update(DBC.Parsers.TABLE, values, whereClause, arrayOf(parserParam.id.toString()))
        Log.i(this.javaClass.name, "Updated $recordsUpdated records")
    }

    fun delParser(id: Int) {
        val whereClause = DBC.Parsers.C_ID + " = ?"
        val db = this.writableDatabase
        db.delete(DBC.Parsers.TABLE, whereClause, arrayOf(id.toString()))
    }

    //---------------------------- Links --------------------------------------------
    fun addOrUpdateLink(title: String, category: String) {
        val db = this.writableDatabase
        db.execSQL("INSERT INTO ${DBC.Links.TABLE} VALUES($title, $category) ON CONFLICT(${DBC.Links.C_TITLE}) DO UPDATE SET ${DBC.Links.C_CATEGORY}=$category")
        db.close()
    }

    fun getLinks(categoryTitle: String): Cursor? {
        val db = this.readableDatabase
        return if (categoryTitle == "System_ALL") {
            db.rawQuery("SELECT * FROM ${DBC.Links.TABLE} ORDER BY ${DBC.Links.C_TITLE}", null)
        } else db.rawQuery("SELECT * FROM ${DBC.Links.TABLE} WHERE ${DBC.Links.C_CATEGORY} = $categoryTitle ORDER BY ${DBC.Links.C_TITLE}", null)
    }

    fun delLink(linkTitle: String) {
        val whereClause = DBC.Links.C_TITLE + " = ?"
        val db = this.writableDatabase
        db.delete(DBC.Links.TABLE, whereClause, arrayOf(linkTitle))
    }
    //---------------------------- Notifications-------------------------------------

    fun addOrUpdateNotification(notif: NotificationDB) {
        val db = this.writableDatabase
        val tmpDate = dateToSQLite(notif.date)
        val tmpStr1 = "INSERT INTO ${DBC.Notifs.TABLE} VALUES(${notif.id}, '${notif.packag}', '${notif.title}', '${notif.text}', '${tmpDate}') ON CONFLICT(${DBC.Notifs.C_ID}) "
        val tmpStr2 = "DO UPDATE SET ${DBC.Notifs.C_PACKAGE}='${notif.packag}', ${DBC.Notifs.C_TITLE}='${notif.title}', ${DBC.Notifs.C_TEXT}='${notif.text}', ${DBC.Notifs.C_DATE}='${tmpDate}'"
        db.execSQL(tmpStr1+tmpStr2)
        db.close()
    }

    fun getNotifications(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM ${DBC.Notifs.TABLE} ORDER BY datetime(${DBC.Notifs.C_DATE}) DESC", null)
    }

}