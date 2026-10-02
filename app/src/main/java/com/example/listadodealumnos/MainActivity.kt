package com.example.listadodealumnos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadodealumnos.databinding.ActivityMainBinding
import com.example.listadodealumnos.databinding.DialogAlumnoBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelperAlumno
    private lateinit var adapter: AlumnoAdapter
    private var listaAlumnos: ArrayList<Alumno> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.main.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        dbHelper = DBHelperAlumno(this)

        configurarRecyclerView()
        cargarAlumnos()

        binding.fabAgregar.setOnClickListener {
            mostrarDialogoAlumno(null)
        }
    }

    private fun configurarRecyclerView() {
        adapter = AlumnoAdapter(
            listaAlumnos = listaAlumnos,
            onEditClick = { alumno -> mostrarDialogoAlumno(alumno) },
            onDeleteClick = { alumno -> mostrarDialogoConfirmacionEliminar(alumno) },
        )

        binding.rvAlumnos.layoutManager = LinearLayoutManager(this)
        binding.rvAlumnos.adapter = adapter
    }

    private fun cargarAlumnos() {
        listaAlumnos = dbHelper.obtenerAlumnos()
        if (listaAlumnos.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvAlumnos.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvAlumnos.visibility = View.VISIBLE
        }
        adapter.actualizarLista(listaAlumnos)
    }

    private fun mostrarDialogoAlumno(alumnoEditar: Alumno?) {
        val dialogBinding = DialogAlumnoBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (alumnoEditar != null) {
            dialogBinding.tvTituloDialog.text = getString(R.string.edit_alumno)
            dialogBinding.etNombre.setText(alumnoEditar.nombre)
            dialogBinding.etCuenta.setText(alumnoEditar.cuenta)
            dialogBinding.etCorreo.setText(alumnoEditar.correo)
            dialogBinding.etImagen.setText(alumnoEditar.imagen)
        } else {
            dialogBinding.tvTituloDialog.text = getString(R.string.add_alumno)
        }

        dialogBinding.btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnGuardar.setOnClickListener {
            val nombre = dialogBinding.etNombre.text.toString().trim()
            val cuenta = dialogBinding.etCuenta.text.toString().trim()
            val correo = dialogBinding.etCorreo.text.toString().trim()
            val imagen = dialogBinding.etImagen.text.toString().trim()

            if (nombre.isEmpty() || cuenta.isEmpty()) {
                Toast.makeText(this, "Por favor completa el nombre y no. de cuenta 💕", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (alumnoEditar != null) {
                val alumnoActualizado = Alumno(
                    id = alumnoEditar.id,
                    nombre = nombre,
                    cuenta = cuenta,
                    correo = correo,
                    imagen = imagen,
                )
                val filasAfectadas = dbHelper.actualizarAlumno(alumnoActualizado)
                if (filasAfectadas > 0) {
                    Toast.makeText(this, "Alumno actualizado con éxito ✨", Toast.LENGTH_SHORT).show()
                    cargarAlumnos()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Error al actualizar alumno", Toast.LENGTH_SHORT).show()
                }
            } else {
                val nuevoAlumno = Alumno(
                    nombre = nombre,
                    cuenta = cuenta,
                    correo = correo,
                    imagen = imagen,
                )
                val idInsertado = dbHelper.insertarAlumno(nuevoAlumno)
                if (idInsertado > 0) {
                    Toast.makeText(this, "Alumno registrado con éxito 💕", Toast.LENGTH_SHORT).show()
                    cargarAlumnos()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Error al guardar alumno", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun mostrarDialogoConfirmacionEliminar(alumno: Alumno) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_alumno)
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(R.string.eliminar) { _, _ ->
                val resultado = dbHelper.eliminarAlumno(alumno.id)
                if (resultado > 0) {
                    Toast.makeText(this, "Alumno eliminado correctamente", Toast.LENGTH_SHORT).show()
                    cargarAlumnos()
                } else {
                    Toast.makeText(this, "Error al eliminar el alumno", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}