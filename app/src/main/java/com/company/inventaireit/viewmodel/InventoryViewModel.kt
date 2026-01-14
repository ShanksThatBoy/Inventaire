package com.company.inventaireit.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.company.inventaireit.data.InventoryDatabase
import com.company.inventaireit.data.entity.ScannedItemEntity
import com.company.inventaireit.data.entity.SessionEntity
import com.company.inventaireit.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository
    
    private val _currentSessionId = MutableLiveData<Long?>()
    val currentSessionId: LiveData<Long?> = _currentSessionId

    val scannedItems: LiveData<List<ScannedItemEntity>> = _currentSessionId.switchMap { id ->
        if (id != null) {
            repository.getItemsForSession(id)
        } else {
            MutableLiveData(emptyList())
        }
    }

    // Informations temporaires de l'opérateur (Étage / Bureau)
    val currentFloor = MutableLiveData("RDC")
    val currentRoom = MutableLiveData("101")
    val currentSite = MutableLiveData("Acoss - Marseille")

    init {
        val dao = InventoryDatabase.getDatabase(application).inventoryDao()
        repository = InventoryRepository(dao)
    }

    fun startNewSession(site: String, operator: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.createSession("Inventaire $site", site, operator)
            _currentSessionId.postValue(id)
            currentSite.postValue(site)
        }
    }

    fun scanItem(code: String) {
        val sessionId = _currentSessionId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val item = ScannedItemEntity(
                sessionId = sessionId,
                codeValue = code,
                site = currentSite.value ?: "",
                floor = currentFloor.value ?: "",
                room = currentRoom.value ?: "",
                operatorName = ""
            )
            repository.insertItem(item)
        }
    }

    fun setCurrentSession(id: Long) {
        _currentSessionId.value = id
    }
}