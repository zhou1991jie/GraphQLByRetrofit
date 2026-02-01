package com.example.grapqldemo6.presenter

import com.example.grapqldemo6.data.model.PokemonData
import com.example.grapqldemo6.data.model.PokemonSpecies
import com.example.grapqldemo6.domain.usecase.PokemonUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import android.content.Context
import android.util.Log
import org.mockito.MockedStatic
import org.mockito.Mockito

class PokemonViewModelTest {

    private lateinit var viewModel: PokemonViewModel
    @Mock
    private lateinit var mockUseCase: PokemonUseCase
    @Mock
    private lateinit var mockContext: Context
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockedLog: MockedStatic<Log>

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedLog.`when`<Int> { Log.e(any(), any(), any()) }.thenReturn(0)
        `when`(mockContext.getString(any())).thenReturn("Mock error message")
        viewModel = PokemonViewModel(mockUseCase, mockContext)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanup() {
        mockedLog.close()
        Dispatchers.resetMain()
    }


    @Test
    fun `searchPokemon should handle success`() = runTest {
        // Arrange
        val testSpecies = PokemonSpecies(
            id = 1,
            name = "pikachu",
            capture_rate = 190,
            pokemon_v2_pokemoncolor = null,
            pokemon_v2_pokemons = emptyList()
        )
        val testData = PokemonData(listOf(testSpecies))
        `when`(mockUseCase.searchPokemonByName(any(), any())).thenReturn(Result.success(testData))

        // Act
        viewModel.updateSearchText("pikachu")
        viewModel.searchPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue("State should be Success after search success", state is PokemonState.Success)
        val successState = state as PokemonState.Success
        assertEquals("Should have 1 result", 1, successState.results.size)
        assertTrue("Should have next page", successState.hasNextPage)
        assertTrue("Should have searched", successState.hasSearched)
    }


    @Test
    fun `updateSearchText should reset state to idle`() = runTest {
        // Arrange
        val testSpecies = PokemonSpecies(
            id = 1,
            name = "pikachu",
            capture_rate = 190,
            pokemon_v2_pokemoncolor = null,
            pokemon_v2_pokemons = emptyList()
        )
        val testData = PokemonData(listOf(testSpecies))
        `when`(mockUseCase.searchPokemonByName(any(), any())).thenReturn(Result.success(testData))

        // Act 1: Search with success
        viewModel.updateSearchText("pikachu")
        viewModel.searchPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 1: Success state should be present
        var state = viewModel.state.first()
        assertTrue("State should be Success after search success", state is PokemonState.Success)

        // Act 2: Update search text
        viewModel.updateSearchText("charizard")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 2: State should be reset to idle
        state = viewModel.state.first()
        assertTrue("State should be Idle after update search text", state is PokemonState.Idle)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `searchPokemon should handle use case error`() = runTest {
        // Arrange
        val errorMessage = "网络错误"
        `when`(mockUseCase.searchPokemonByName(any(), any())).thenReturn(Result.failure(Exception(errorMessage)))

        // Act
        viewModel.updateSearchText("pikachu")
        viewModel.searchPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.first()
        assertTrue("State should be Error after search failure", state is PokemonState.Error)
        val errorState = state as PokemonState.Error
        assertTrue("Error message should not be empty", errorState.message.isNotEmpty())
    }


    @Test
    fun `clearError should reset to idle state`() = runTest {
        // Arrange
        val errorMessage = "网络错误"
        `when`(mockUseCase.searchPokemonByName(any(), any())).thenReturn(Result.failure(Exception(errorMessage)))

        // Act 1: Trigger error
        viewModel.updateSearchText("pikachu")
        viewModel.searchPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 1: Error state should be present
        var state = viewModel.state.first()
        assertTrue("State should be Error after search failure", state is PokemonState.Error)

        // Act 2: Clear error
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 2: State should be reset to idle
        state = viewModel.state.first()
        assertTrue("State should be Idle after clearError", state is PokemonState.Idle)
    }
}
