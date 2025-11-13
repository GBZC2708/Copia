package com.example.alphakids.data.firebase.repository

import android.util.Log
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.repository.CreateStudentResult
import com.example.alphakids.domain.repository.StudentRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : StudentRepository {

    private val estudiantesCol = db.collection("estudiantes")
    private val usuariosCol = db.collection("usuarios")
    private val docentesCol = db.collection("docentes")

    override suspend fun createStudent(estudiante: Estudiante): CreateStudentResult {
        return try {
            val documentReference = estudiantesCol.add(estudiante).await()
            Log.d("StudentRepo", "Estudiante creado con ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e("StudentRepo", "Error al crear estudiante", e)
            Result.failure(e)
        }
    }

    override fun getStudentsForTutor(tutorId: String): Flow<List<Estudiante>> {
        Log.d("StudentRepo", "Fetching students for tutor ID: $tutorId")
        val query: Query = estudiantesCol.whereEqualTo("id_tutor", tutorId)
        return query.snapshots().map { querySnapshot -> // <-- Usa snapshots() y map()
            Log.d("StudentRepo", "Snapshot received. Documents found: ${querySnapshot.size()}")
            if (querySnapshot.metadata.hasPendingWrites()) {
                Log.d("StudentRepo", "Snapshot has pending writes.")
            }
            val students = querySnapshot.toObjects(Estudiante::class.java)
            Log.d("StudentRepo", "Mapped ${students.size} students")
            students
        }.catch { exception ->
            Log.e("StudentRepo", "Error in student flow for tutor $tutorId", exception)
            emit(emptyList())
        }
    }

    override fun getDocentes(institucionId: String?): Flow<List<Pair<String, String>>> = flow {
        val usuariosQuery = usuariosCol.whereEqualTo("rol", "docente")

        val usuariosSnapshot = usuariosQuery.get().await()
        val usuarios = usuariosSnapshot.documents.mapNotNull { document ->
            val uid = document.id
            val nombre = document.getString("nombre")?.trim().orEmpty()
            val apellido = document.getString("apellido")?.trim().orEmpty()
            if (nombre.isBlank() && apellido.isBlank()) {
                null
            } else {
                uid to listOf(nombre, apellido)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
            }
        }

        val filteredUsuarios = if (institucionId.isNullOrBlank()) {
            usuarios
        } else {
            val docentesSnapshot = docentesCol.whereEqualTo("idInstitucion", institucionId).get().await()
            val docentesIds = docentesSnapshot.documents.map { it.id }.toSet()
            usuarios.filter { docentesIds.contains(it.first) }
        }

        emit(filteredUsuarios.sortedBy { it.second.lowercase() })
    }
}
