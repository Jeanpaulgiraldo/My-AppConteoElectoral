package com.example.myapplication.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.Candidate
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.ElectionStage
import com.example.myapplication.viewmodel.ElectionUiState
import com.example.myapplication.viewmodel.ElectoralViewModel
import com.example.myapplication.viewmodel.WinnerResult

@Composable
fun ElectionScreen(
    viewModel: ElectoralViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ElectionScreen(
        uiState = uiState,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionScreen(
    uiState: ElectionUiState,
    viewModel: ElectoralViewModel? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HowToVote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Conteo Electoral",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Elección de Alcalde - Colombia",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.stage) {
                ElectionStage.SETUP -> SetupScreen(uiState = uiState, viewModel = viewModel)
                ElectionStage.VOTING -> VotingScreen(uiState = uiState, viewModel = viewModel)
                ElectionStage.RESULTS -> ResultsScreen(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}

/**
 * PANTALLA 1: CONFIGURACIÓN DE LA ELECCIÓN (SETUP)
 * Utiliza EditText (OutlinedTextField), TextView (Text) y Button (Button).
 */
@Composable
fun SetupScreen(
    uiState: ElectionUiState,
    viewModel: ElectoralViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TextView explicativo
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Configuración de las Elecciones a la Alcaldía",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ingrese el nombre del Municipio de Colombia, los 3 candidatos inscritos y el número total de electores antes de iniciar la votación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
            }
        }

        // EditText: Nombre del Municipio
        OutlinedTextField(
            value = uiState.municipio,
            onValueChange = { viewModel?.updateMunicipio(it) },
            label = { Text("EditText: Nombre del Municipio") },
            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // EditText: Número de Electores (Ingresado por teclado)
        OutlinedTextField(
            value = uiState.totalElectorsInput,
            onValueChange = { viewModel?.updateTotalElectorsInput(it) },
            label = { Text("EditText: Número Total de Electores") },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // TextView: Encabezado de Candidatos
        Text(
            text = "TextView: Tres (3) Candidatos Inscritos:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // EditText: Candidato 1
        OutlinedTextField(
            value = uiState.candidate1Name,
            onValueChange = { viewModel?.updateCandidate1Name(it) },
            label = { Text("EditText: Nombre Candidato 1") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // EditText: Candidato 2
        OutlinedTextField(
            value = uiState.candidate2Name,
            onValueChange = { viewModel?.updateCandidate2Name(it) },
            label = { Text("EditText: Nombre Candidato 2") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // EditText: Candidato 3
        OutlinedTextField(
            value = uiState.candidate3Name,
            onValueChange = { viewModel?.updateCandidate3Name(it) },
            label = { Text("EditText: Nombre Candidato 3") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de estado / error
        uiState.statusMessage?.let { msg ->
            Text(
                text = msg,
                color = if (uiState.isErrorMessage) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Button: Iniciar Votaciones
        Button(
            onClick = { viewModel?.startElection() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(Icons.Default.HowToVote, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Button: Iniciar Proceso de Votación",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * PANTALLA 2: PROCESO DE VOTACIÓN (VOTING)
 * Incluye validación de edad (impedir voto a menor de edad), selección de candidato y botón de terminar.
 */
@Composable
fun VotingScreen(
    uiState: ElectionUiState,
    viewModel: ElectoralViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Indicador de Proceso y Municipio
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Elección de Alcalde - ${uiState.municipio}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TextView: Elector ${uiState.currentElectorNumber} de ${uiState.totalElectors}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Votos: ${uiState.totalVotesCast}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // VALIDACIÓN DE EDAD (REQUISITO FUNDAMENTAL)
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TextView: Validación de Mayoría de Edad (>= 18 años)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // EditText: Edad del Elector
                OutlinedTextField(
                    value = uiState.voterAgeInput,
                    onValueChange = { viewModel?.updateVoterAgeInput(it) },
                    label = { Text("EditText: Ingrese la Edad del Elector (años)") },
                    placeholder = { Text("Ej: 22") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Alerta si el elector es menor de edad o hay error
        AnimatedVisibility(visible = uiState.statusMessage != null) {
            uiState.statusMessage?.let { msg ->
                Surface(
                    color = if (uiState.isErrorMessage) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (uiState.isErrorMessage) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (uiState.isErrorMessage) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.isErrorMessage) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // SELECCIÓN DE CANDIDATO
        Text(
            text = "TextView: Seleccione el candidato por el cual vota:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        uiState.candidates.forEach { candidate ->
            val isSelected = uiState.selectedCandidateId == candidate.id

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { viewModel?.selectCandidate(candidate.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel?.selectCandidate(candidate.id) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = candidate.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Aspirante a la Alcaldía",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button: Registrar Voto
        Button(
            onClick = { viewModel?.registerVote() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Button: Registrar Voto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Button: Terminar Votaciones (Requisito del enunciado)
        OutlinedButton(
            onClick = { viewModel?.finishElectionEarly() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.StopCircle, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Button: Terminar Proceso de Votaciones",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * PANTALLA 3: PRODUCTO FINAL Y RESULTADOS (RESULTS)
 * Muestra el candidato ganador y por cuántos votos ganó las elecciones.
 */
@Composable
fun ResultsScreen(
    uiState: ElectionUiState,
    viewModel: ElectoralViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta del Ganador (Producto Final)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "RESULTADO FINAL DE LAS ELECCIONES",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                Text(
                    text = "Alcaldía de ${uiState.municipio}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (val result = uiState.winnerResult) {
                    is WinnerResult.SingleWinner -> {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "🏆 ¡GANADOR: ${result.winner.name}! 🏆",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Obtuvo un total de ${result.winner.votes} votos.",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // INDICACIÓN EXACTA PEDIDA EN EL ENUNCIADO: Por cuántos votos ha ganado
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "✨ Ganó por una diferencia de ${result.margin} ${if (result.margin == 1) "voto" else "votos"} sobre ${result.runnerUp.name} (segundo lugar).",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    is WinnerResult.Tie -> {
                        val names = result.tiedCandidates.joinToString(" y ") { it.name }
                        Text(
                            text = "🤝 ¡EMPATE ELECTORAL!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hubo un empate entre $names con ${result.votes} votos cada uno.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                    WinnerResult.NoVotes -> {
                        Text(
                            text = "No se registraron votos en esta elección.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    null -> {}
                }
            }
        }

        // DESGLOSE COMPLETO DE VOTOS
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TextView: Desglose Oficial de Votación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                uiState.candidates.forEach { candidate ->
                    val percentage = if (uiState.totalVotesCast > 0) {
                        (candidate.votes.toFloat() / uiState.totalVotesCast * 100)
                    } else 0f

                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = candidate.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${candidate.votes} votos (${String.format("%.1f%%", percentage)})",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { if (uiState.totalVotesCast > 0) candidate.votes.toFloat() / uiState.totalVotesCast else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }

                if (uiState.rejectedMinorsCount > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "🚫 Intentos de voto por menores de edad bloqueados: ${uiState.rejectedMinorsCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button: Reiniciar Nueva Elección
        Button(
            onClick = { viewModel?.resetElection() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Default.RestartAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Button: Iniciar Nueva Elección",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ElectionScreenPreview() {
    MyApplicationTheme {
        ElectionScreen(
            uiState = ElectionUiState()
        )
    }
}
