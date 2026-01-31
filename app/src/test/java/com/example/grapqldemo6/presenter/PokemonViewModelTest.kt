package com.example.grapqldemo6.presenter

import com.example.grapqldemo6.data.PokemonRepository
import com.example.grapqldemo6.data.model.PokemonData
import com.example.grapqldemo6.data.model.PokemonSpecies
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
import kotlinx.coroutines.flow.flow

class PokemonViewModelTest {

    private lateinit var viewModel: PokemonViewModel
    @Mock
    private lateinit var mockRepository: PokemonRepository
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = PokemonViewModel(mockRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchPokemon should handle repository error`() = runTest {
        // Arrange
        val errorMessage = "网络错误"
        `when`(mockRepository.searchPokemonByName(any(), any())).thenReturn(Result.failure(Exception(errorMessage)))

        // Act
        viewModel.updateSearchText("pikachu")
        viewModel.searchPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val error = viewModel.errorMessage.first()
        assertEquals(errorMessage, error)
        val searchResults = viewModel.searchResults.first()
        assertTrue(searchResults.isEmpty())
        val hasNextPage = viewModel.hasNextPage.first()
        assertFalse(hasNextPage)
    }

    @Test
    fun `loadNextPage should handle repository error`() = runTest {
        // Arrange
        val errorMessage = "加载更多失败"
        `when`(mockRepository.searchPokemonByName(any(), any())).thenReturn(Result.failure(Exception(errorMessage)))

        // Act
        viewModel.updateSearchText("pikachu")
        viewModel.loadNextPage()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val error = viewModel.errorMessage.first()
        assertEquals(errorMessage, error)
        val hasNextPage = viewModel.hasNextPage.first()
        assertFalse(hasNextPage)
    }

    @Test
    fun `clearError should remove error message`() = runTest {
        // Arrange
        val errorMessage = "测试错误"
        `when`(mockRepository.searchPokemonByName(any(), any())).thenReturn(Result.failure(Exception(errorMessage)))

        // Act 1: Trigger error
        viewModel.updateSearchText("pikachu")
        viewModel.searchPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 1: Error should be present
        var error = viewModel.errorMessage.first()
        assertEquals(errorMessage, error)

        // Act 2: Clear error
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 2: Error should be cleared
        error = viewModel.errorMessage.first()
        assertNull(error)
    }

    @Test
    fun `updateSearchText should clear error message`() = runTest {
        // Arrange
        val errorMessage = "测试错误"
        `when`(mockRepository.searchPokemonByName(any(), any())).thenReturn(Result.failure(Exception(errorMessage)))

        // Act 1: Trigger error
        viewModel.updateSearchText("pikachu")
        viewModel.searchPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 1: Error should be present
        var error = viewModel.errorMessage.first()
        assertEquals(errorMessage, error)

        // Act 2: Update search text
        viewModel.updateSearchText("charizard")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 2: Error should be cleared
        error = viewModel.errorMessage.first()
        assertNull(error)
    }

    @Test
    fun `searchPokemon should clear error on success`() = runTest {
        // Arrange
        val testSpecies = PokemonSpecies(
            id = 1,
            name = "pikachu",
            capture_rate = 190,
            pokemon_v2_pokemoncolor = null,
            pokemon_v2_pokemons = emptyList()
        )
        val testData = PokemonData(listOf(testSpecies))
        `when`(mockRepository.searchPokemonByName(any(), any())).thenReturn(Result.success(testData))

        // Act 1: Trigger error first
        `when`(mockRepository.searchPokemonByName(any(), any())).thenReturn(Result.failure(Exception("测试错误")))
        viewModel.updateSearchText("pikachu")
        viewModel.searchPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 1: Error should be present
        var error = viewModel.errorMessage.first()
        assertNotNull(error)

        // Act 2: Search with success
        `when`(mockRepository.searchPokemonByName(any(), any())).thenReturn(Result.success(testData))
        viewModel.updateSearchText("pikachu")
        viewModel.searchPokemon()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert 2: Error should be cleared
        error = viewModel.errorMessage.first()
        assertNull(error)
        val searchResults = viewModel.searchResults.first()
        assertEquals(1, searchResults.size)
    }
}
