package com.jwmaila.appticketera.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jwmaila.appticketera.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    eventId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var ticketPrice by remember { mutableStateOf("") }
    var totalSeats by remember { mutableStateOf("100") }
    var imageUrl by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val createState by viewModel.createState.collectAsState()
    
    LaunchedEffect(eventId) {
        if (eventId != null) {
            viewModel.loadEvent(eventId)
        }
    }
    
    LaunchedEffect(createState) {
        if (createState is CreateEventState.EventLoaded) {
            val event = (createState as CreateEventState.EventLoaded).event
            name = event.name
            description = event.description ?: ""
            date = event.date
            venue = event.venue
            ticketPrice = event.ticketPrice.toString()
            totalSeats = event.totalSeats.toString()
            imageUrl = event.imageUrl ?: ""
        } else if (createState is CreateEventState.Success) {
            scope.launch {
                snackbarHostState.showSnackbar("Evento creado exitosamente")
                delay(400)
                onNavigateBack()
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (eventId == null) "Crear Evento" else "Editar Evento",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MediumBlue,
                    titleContentColor = White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (createState is CreateEventState.Error) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = (createState as CreateEventState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = ErrorRed
                    )
                }
            }
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del evento *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5
            )
            
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Fecha (YYYY-MM-DD) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("2024-12-31") }
            )
            
            OutlinedTextField(
                value = venue,
                onValueChange = { venue = it },
                label = { Text("Lugar *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = ticketPrice,
                    onValueChange = { ticketPrice = it },
                    label = { Text("Precio *") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("$") },
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = totalSeats,
                    onValueChange = { totalSeats = it },
                    label = { Text("Asientos *") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL de imagen") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                placeholder = { Text("https://...") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    if (eventId == null) {
                        viewModel.createEvent(
                            name = name,
                            description = description.ifBlank { null },
                            date = date,
                            venue = venue,
                            ticketPrice = ticketPrice.toDoubleOrNull() ?: 0.0,
                            totalSeats = totalSeats.toIntOrNull() ?: 100,
                            imageUrl = imageUrl.ifBlank { null }
                        )
                    } else {
                        viewModel.updateEvent(
                            eventId = eventId,
                            name = name,
                            description = description.ifBlank { null },
                            date = date,
                            venue = venue,
                            ticketPrice = ticketPrice.toDoubleOrNull() ?: 0.0,
                            totalSeats = totalSeats.toIntOrNull() ?: 100,
                            imageUrl = imageUrl.ifBlank { null }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MediumBlue),
                shape = RoundedCornerShape(12.dp),
                enabled = createState !is CreateEventState.Loading &&
                        name.isNotBlank() && date.isNotBlank() && 
                        venue.isNotBlank() && ticketPrice.isNotBlank()
            ) {
                if (createState is CreateEventState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = White
                    )
                } else {
                    Text(
                        if (eventId == null) "Crear Evento" else "Guardar Cambios",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
