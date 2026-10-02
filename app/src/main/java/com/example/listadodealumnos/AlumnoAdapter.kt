package com.example.listadodealumnos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listadodealumnos.databinding.ItemPersonaBinding

class AlumnoAdapter(
    private var listaAlumnos: ArrayList<Alumno>,
    private val onEditClick: (Alumno) -> Unit,
    private val onDeleteClick: (Alumno) -> Unit,
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    class AlumnoViewHolder(val binding: ItemPersonaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val binding = ItemPersonaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return AlumnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = listaAlumnos[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvNombre.text = alumno.nombre
            tvCuenta.text = context.getString(R.string.cuenta_format, alumno.cuenta)
            tvCorreo.text = alumno.correo

            if (alumno.imagen.isNotBlank()) {
                Glide.with(context)
                    .load(alumno.imagen)
                    .placeholder(R.drawable.ic_person_placeholder)
                    .error(R.drawable.ic_person_placeholder)
                    .centerCrop()
                    .into(ivFoto)
            } else {
                ivFoto.setImageResource(R.drawable.ic_person_placeholder)
            }

            btnEditar.setOnClickListener { onEditClick(alumno) }
            btnEliminar.setOnClickListener { onDeleteClick(alumno) }
        }
    }

    override fun getItemCount(): Int = listaAlumnos.size

    fun actualizarLista(nuevaLista: ArrayList<Alumno>) {
        listaAlumnos = nuevaLista
        notifyDataSetChanged()
    }
}