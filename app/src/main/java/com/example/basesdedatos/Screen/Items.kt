package com.example.basesdedatos.Screen

import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basesdedatos.DAO.UserDao
import com.example.basesdedatos.Model.User
import com.example.basesdedatos.Repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.text.StringBuilder


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserApp(userRepository: UserRepository) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var deleteId by remember { mutableStateOf("") }
    var editId by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var editableUsuario by remember { mutableStateOf<User?>(null) }

    var users by remember { mutableStateOf(emptyList<User>()) }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(Color(0x80FFFBF2)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(text = "Bienvenido a la base de datos",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(16.dp)
                )
        // Ingresar los datos del usuario
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0x80FAE3EB),
                unfocusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .width(290.dp)
                .height(70.dp),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0x80FAE3EB),
                unfocusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .width(290.dp)
                .height(70.dp),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0x80FAE3EB),
                unfocusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .width(290.dp)
                .height(70.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Boton para registrar un usuario
        Button(onClick = {
            val user = User(
                nombre = nombre,
                apellido = apellido,
                edad = edad.toIntOrNull() ?: 0
            )
            scope.launch {
                withContext(Dispatchers.IO) {
                    userRepository.insert(user)
                }
                // actualizar la lista despues de u¿insertar un usario
                users = userRepository.getAllUsers()
            }
            Toast.makeText(context, "Usuario registrado", Toast.LENGTH_SHORT).show()
        },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x80E0F7E6), // Color de fondo
            contentColor = Color.Black // Color del texto
                )
        ) {
            Text(text = "Registrar")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Boton para listar usuarios
        Button(onClick = {
            scope.launch {
                users = withContext(Dispatchers.IO) {
                    userRepository.getAllUsers()
                }
            }
        },         colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x80E0F7E6), // Color de fondo
            contentColor = Color.Black // Color del texto
            )
        ) {
            Text(text = "Listar")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Ingresar el ID para eliminar un usuario
        TextField(
            value = deleteId,
            onValueChange = { deleteId = it },
            label = { Text("Id para eliminar") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0x80E0F8D2),
                unfocusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .width(290.dp)
                .height(70.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            IconButton(onClick = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        userRepository.deleteById(deleteId.toInt())
                    }
                    // Actualizar la lista despues de eliminar un usuario
                    users = userRepository.getAllUsers()
                }
            }) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Editar un usuario
        TextField(
            value = editId,
            onValueChange = { editId = it },
            label = { Text("Id para editar") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0x80E0F8D2),
                unfocusedIndicatorColor = Color.Transparent),
            modifier = Modifier
                .width(290.dp)
                .height(70.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            IconButton(onClick = {
                // Buscar el usuario
                editableUsuario = users.find { it.id == editId.toInt() }
                if (editableUsuario != null) {
                    // Pre-fill the fields with user data
                    nombre = editableUsuario!!.nombre
                    apellido = editableUsuario!!.apellido
                    edad = editableUsuario!!.edad.toString()
                    showDialog = true
                } else {
                    Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar")
            }

        }
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de usuarios
        Column {
            users.forEach { user ->
                Text(text = "${user.id}: ${user.nombre} ${user.apellido} - ${user.edad} años")
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cuadro de dialogo para editar un usuario
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Editar usuario") },
                text = {
                    Column {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0x80FAE3EB),
                                unfocusedIndicatorColor = Color.Transparent),
                            modifier = Modifier
                                .width(290.dp)
                                .height(70.dp)
                                .padding(5.dp),
                            shape = RoundedCornerShape(12.dp)

                        )
                        TextField(
                            value = apellido,
                            onValueChange = { apellido = it },
                            label = { Text("Apellido") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0x80FAE3EB),
                                unfocusedIndicatorColor = Color.Transparent),
                            modifier = Modifier
                                .width(290.dp)
                                .height(70.dp)
                                .padding(5.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                        TextField(
                            value = edad,
                            onValueChange = { edad = it },
                            label = { Text("Edad") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0x80FAE3EB),
                                unfocusedIndicatorColor = Color.Transparent),
                            modifier = Modifier
                                .width(290.dp)
                                .height(70.dp)
                                .padding(5.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val updatedUser = editableUsuario?.copy(
                            nombre = nombre,
                            apellido = apellido,
                            edad = edad.toIntOrNull() ?: 0
                        )
                        if (updatedUser != null) {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    userRepository.updateById(
                                        updatedUser.id,
                                        updatedUser.nombre,
                                        updatedUser.apellido,
                                        updatedUser.edad
                                    )
                                    // Actualizar la lista despues de editar un usuario
                                    users = userRepository.getAllUsers()
                                }
                            }
                            editableUsuario = null
                            showDialog = false
                        }
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                },
                containerColor = Color(0xBFADD8E6), // Cambiar el color de fondo del diálogo
                tonalElevation = 8.dp // Cambiar la elevación del diálogo (sombra)
            )
        }
    }
}
