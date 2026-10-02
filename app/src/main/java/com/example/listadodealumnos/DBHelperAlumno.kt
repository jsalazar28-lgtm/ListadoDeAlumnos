package com.example.listadodealumnos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperAlumno(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AlumnosDB.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ALUMNOS = "Alumnos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CUENTA = "cuenta"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_ALUMNOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CUENTA TEXT NOT NULL,
                $COLUMN_CORREO TEXT NOT NULL,
                $COLUMN_IMAGEN TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)

        // Pre-populación inicial de datos
        insertarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ALUMNOS")
        onCreate(db)
    }

    private fun insertarDatosIniciales(db: SQLiteDatabase?) {
        val alumnosIniciales = listOf(
            Alumno(
                nombre = "Sofía Martínez",
                cuenta = "20214589",
                correo = "sofia.martinez@ucol.mx",
                imagen = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
            ),
            Alumno(
                nombre = "Valeria Gómez",
                cuenta = "20203672",
                correo = "valeria.gomez@ucol.mx",
                imagen = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400",
            ),
        )

        for (alumno in alumnosIniciales) {
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE, alumno.nombre)
                put(COLUMN_CUENTA, alumno.cuenta)
                put(COLUMN_CORREO, alumno.correo)
                put(COLUMN_IMAGEN, alumno.imagen)
            }
            db?.insert(TABLE_ALUMNOS, null, values)
        }
    }

    fun insertarAlumno(alumno: Alumno): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val result = db.insert(TABLE_ALUMNOS, null, values)
        db.close()
        return result
    }

    fun obtenerAlumnos(): ArrayList<Alumno> {
        val listaAlumnos = ArrayList<Alumno>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ALUMNOS ORDER BY $COLUMN_ID DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(COLUMN_ID)
                val nombreIndex = cursor.getColumnIndex(COLUMN_NOMBRE)
                val cuentaIndex = cursor.getColumnIndex(COLUMN_CUENTA)
                val correoIndex = cursor.getColumnIndex(COLUMN_CORREO)
                val imagenIndex = cursor.getColumnIndex(COLUMN_IMAGEN)

                val id = if (idIndex != -1) cursor.getInt(idIndex) else 0
                val nombre = if (nombreIndex != -1) cursor.getString(nombreIndex) else ""
                val cuenta = if (cuentaIndex != -1) cursor.getString(cuentaIndex) else ""
                val correo = if (correoIndex != -1) cursor.getString(correoIndex) else ""
                val imagen = if (imagenIndex != -1) cursor.getString(imagenIndex) else ""

                listaAlumnos.add(Alumno(id, nombre, cuenta, correo, imagen))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return listaAlumnos
    }

    fun actualizarAlumno(alumno: Alumno): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val result = db.update(TABLE_ALUMNOS, values, "$COLUMN_ID = ?", arrayOf(alumno.id.toString()))
        db.close()
        return result
    }

    fun eliminarAlumno(id: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_ALUMNOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}