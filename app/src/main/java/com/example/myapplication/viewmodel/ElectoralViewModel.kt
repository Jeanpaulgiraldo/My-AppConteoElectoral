package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Candidate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ElectionStage {
    SETUP,      // Configuración de electores y nombres de candidatos
    VOTING,     // Proceso de votación con validación de edad
    RESULTS     // Pantalla final con el ganador y la diferencia de votos
}

data class ElectionUiState(
    val stage: ElectionStage = ElectionStage.SETUP,
    val municipio: String = "Municipio de Colombia",
    val candidate1Name: String = "Candidato 1",
    val candidate2Name: String = "Candidato 2",
    val candidate3Name: String = "Candidato 3",
    val totalElectorsInput: String = "10",
    val totalElectors: Int = 0,
    val currentElectorNumber: Int = 1,
    val voterAgeInput: String = "",
    val selectedCandidateId: Int? = null,
    val statusMessage: String? = null,
    val isErrorMessage: Boolean = false,
    val candidates: List<Candidate> = emptyList(),
    val rejectedMinorsCount: Int = 0
) {
    val totalVotesCast: Int
        get() = candidates.sumOf { it.votes }

    val winnerResult: WinnerResult?
        get() {
            if (candidates.isEmpty()) return null
            val sorted = candidates.sortedByDescending { it.votes }
            val first = sorted[0]
            val second = sorted[1]

            return if (first.votes > second.votes) {
                WinnerResult.SingleWinner(
                    winner = first,
                    runnerUp = second,
                    margin = first.votes - second.votes
                )
            } else if (first.votes > 0 && first.votes == second.votes) {
                val tied = candidates.filter { it.votes == first.votes }
                WinnerResult.Tie(tiedCandidates = tied, votes = first.votes)
            } else {
                WinnerResult.NoVotes
            }
        }
}

sealed class WinnerResult {
    data class SingleWinner(
        val winner: Candidate,
        val runnerUp: Candidate,
        val margin: Int
    ) : WinnerResult()

    data class Tie(
        val tiedCandidates: List<Candidate>,
        val votes: Int
    ) : WinnerResult()

    object NoVotes : WinnerResult()
}

class ElectoralViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ElectionUiState())
    val uiState: StateFlow<ElectionUiState> = _uiState.asStateFlow()

    fun updateMunicipio(municipio: String) {
        _uiState.update { it.copy(municipio = municipio) }
    }

    fun updateCandidate1Name(name: String) {
        _uiState.update { it.copy(candidate1Name = name) }
    }

    fun updateCandidate2Name(name: String) {
        _uiState.update { it.copy(candidate2Name = name) }
    }

    fun updateCandidate3Name(name: String) {
        _uiState.update { it.copy(candidate3Name = name) }
    }

    fun updateTotalElectorsInput(input: String) {
        _uiState.update { it.copy(totalElectorsInput = input) }
    }

    fun updateVoterAgeInput(age: String) {
        _uiState.update { it.copy(voterAgeInput = age, statusMessage = null) }
    }

    fun selectCandidate(candidateId: Int) {
        _uiState.update { it.copy(selectedCandidateId = candidateId, statusMessage = null) }
    }

    fun startElection() {
        val currentState = _uiState.value
        val count = currentState.totalElectorsInput.toIntOrNull()

        if (count == null || count <= 0) {
            _uiState.update {
                it.copy(
                    statusMessage = "Por favor ingrese un número válido de electores (mayor a 0).",
                    isErrorMessage = true
                )
            }
            return
        }

        val c1Name = currentState.candidate1Name.ifBlank { "Candidato 1" }
        val c2Name = currentState.candidate2Name.ifBlank { "Candidato 2" }
        val c3Name = currentState.candidate3Name.ifBlank { "Candidato 3" }

        val initialCandidates = listOf(
            Candidate(id = 1, name = c1Name, votes = 0),
            Candidate(id = 2, name = c2Name, votes = 0),
            Candidate(id = 3, name = c3Name, votes = 0)
        )

        _uiState.update {
            it.copy(
                stage = ElectionStage.VOTING,
                totalElectors = count,
                candidates = initialCandidates,
                currentElectorNumber = 1,
                voterAgeInput = "",
                selectedCandidateId = null,
                statusMessage = null,
                isErrorMessage = false,
                rejectedMinorsCount = 0
            )
        }
    }

    fun registerVote() {
        val currentState = _uiState.value
        val age = currentState.voterAgeInput.toIntOrNull()

        if (age == null) {
            _uiState.update {
                it.copy(
                    statusMessage = "Por favor ingrese una edad válida.",
                    isErrorMessage = true
                )
            }
            return
        }

        // VALIDACIÓN DE MAYORÍA DE EDAD (>= 18 años)
        if (age < 18) {
            _uiState.update {
                it.copy(
                    rejectedMinorsCount = it.rejectedMinorsCount + 1,
                    statusMessage = "⚠️ Acceso denegado: El elector tiene $age años (menor de edad). No está facultado para votar.",
                    isErrorMessage = true,
                    voterAgeInput = "",
                    selectedCandidateId = null
                )
            }
            return
        }

        val candidateId = currentState.selectedCandidateId
        if (candidateId == null) {
            _uiState.update {
                it.copy(
                    statusMessage = "Por favor seleccione un candidato antes de registrar el voto.",
                    isErrorMessage = true
                )
            }
            return
        }

        // Registrar el voto para el candidato seleccionado
        val updatedCandidates = currentState.candidates.map { candidate ->
            if (candidate.id == candidateId) {
                candidate.copy(votes = candidate.votes + 1)
            } else {
                candidate
            }
        }

        val nextElectorNumber = currentState.currentElectorNumber + 1

        if (nextElectorNumber > currentState.totalElectors) {
            // Se han procesado todos los electores
            _uiState.update {
                it.copy(
                    stage = ElectionStage.RESULTS,
                    candidates = updatedCandidates,
                    statusMessage = "Se completó la votación de todos los electores.",
                    isErrorMessage = false
                )
            }
        } else {
            // Avanzar al siguiente elector
            _uiState.update {
                it.copy(
                    candidates = updatedCandidates,
                    currentElectorNumber = nextElectorNumber,
                    voterAgeInput = "",
                    selectedCandidateId = null,
                    statusMessage = "Voto registrado exitosamente para Elector #${currentState.currentElectorNumber}.",
                    isErrorMessage = false
                )
            }
        }
    }

    fun finishElectionEarly() {
        _uiState.update {
            it.copy(
                stage = ElectionStage.RESULTS,
                statusMessage = "Proceso de votación finalizado anticipadamente.",
                isErrorMessage = false
            )
        }
    }

    fun resetElection() {
        _uiState.update {
            ElectionUiState()
        }
    }
}
