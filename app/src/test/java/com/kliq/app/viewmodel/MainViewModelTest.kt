package com.kliq.app.viewmodel

import com.kliq.app.data.repository.UserRepository
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.mock

class MainViewModelTest {

    private val repository: UserRepository = mock(UserRepository::class.java)
    
    @Test
    fun viewModelIsInitialized() {
        val viewModel = MainViewModel(repository)
        assertNotNull(viewModel)
    }
}
