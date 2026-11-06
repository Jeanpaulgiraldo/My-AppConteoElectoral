package com.example.myapplication

import com.example.myapplication.viewmodel.ElectionStage
import com.example.myapplication.viewmodel.ElectoralViewModel
import com.example.myapplication.viewmodel.WinnerResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ElectoralViewModelTest {

    @Test
    fun testStartElectionWithValidElectors() {
        val viewModel = ElectoralViewModel()
        viewModel.updateMunicipio("Medellín")
        viewModel.updateCandidate1Name("Carlos Pérez")
        viewModel.updateCandidate2Name("María Rodríguez")
        viewModel.updateCandidate3Name("Juan Gómez")
        viewModel.updateTotalElectorsInput("5")

        viewModel.startElection()

        val state = viewModel.uiState.value
        assertEquals(ElectionStage.VOTING, state.stage)
        assertEquals("Medellín", state.municipio)
        assertEquals(5, state.totalElectors)
        assertEquals(3, state.candidates.size)
        assertEquals("Carlos Pérez", state.candidates[0].name)
    }

    @Test
    fun testMinorAgeValidationBlocksVote() {
        val viewModel = ElectoralViewModel()
        viewModel.updateTotalElectorsInput("3")
        viewModel.startElection()

        // Elector ingresa edad 16 (menor de edad)
        viewModel.updateVoterAgeInput("16")
        viewModel.selectCandidate(1)
        viewModel.registerVote()

        val state = viewModel.uiState.value
        // Debe denegar el voto y aumentar el contador de menores bloqueados
        assertEquals(0, state.totalVotesCast)
        assertEquals(1, state.rejectedMinorsCount)
        assertTrue(state.isErrorMessage)
        assertTrue(state.statusMessage?.contains("menor de edad") == true)
    }

    @Test
    fun testAdultCanVoteAndWinnerResultIsCorrect() {
        val viewModel = ElectoralViewModel()
        viewModel.updateCandidate1Name("Candidato A")
        viewModel.updateCandidate2Name("Candidato B")
        viewModel.updateCandidate3Name("Candidato C")
        viewModel.updateTotalElectorsInput("3")
        viewModel.startElection()

        // Voto 1: Candidato 1 (Edad: 25)
        viewModel.updateVoterAgeInput("25")
        viewModel.selectCandidate(1)
        viewModel.registerVote()

        // Voto 2: Candidato 1 (Edad: 30)
        viewModel.updateVoterAgeInput("30")
        viewModel.selectCandidate(1)
        viewModel.registerVote()

        // Voto 3: Candidato 2 (Edad: 40)
        viewModel.updateVoterAgeInput("40")
        viewModel.selectCandidate(2)
        viewModel.registerVote()

        val state = viewModel.uiState.value
        assertEquals(ElectionStage.RESULTS, state.stage)
        assertEquals(3, state.totalVotesCast)

        val result = state.winnerResult
        assertTrue(result is WinnerResult.SingleWinner)
        val winnerResult = result as WinnerResult.SingleWinner

        assertEquals("Candidato A", winnerResult.winner.name)
        assertEquals(2, winnerResult.winner.votes)
        assertEquals("Candidato B", winnerResult.runnerUp.name)
        assertEquals(1, winnerResult.runnerUp.votes)
        // Margen de victoria: 2 - 1 = 1 voto
        assertEquals(1, winnerResult.margin)
    }

    @Test
    fun testFinishElectionEarly() {
        val viewModel = ElectoralViewModel()
        viewModel.updateTotalElectorsInput("10")
        viewModel.startElection()

        viewModel.updateVoterAgeInput("20")
        viewModel.selectCandidate(1)
        viewModel.registerVote()

        viewModel.finishElectionEarly()

        assertEquals(ElectionStage.RESULTS, viewModel.uiState.value.stage)
    }
}
