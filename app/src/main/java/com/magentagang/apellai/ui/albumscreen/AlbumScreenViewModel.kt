package com.magentagang.apellai.ui.albumscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class AlbumScreenViewModel(application: Application, id : String) : AndroidViewModel(application) {
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    var repositoryUtils: RepositoryUtils = RepositoryUtils(databaseDao)
    private val viewModelJob = Job()
    val coroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val _album = MutableLiveData<Album?>()
    val album: LiveData<Album?>
        get() = _album

    init {
        coroutineScope.launch {
            val albumDeferred = repositoryUtils.fetchAlbumAsync(id)
            try{
                val albumVal = albumDeferred.await()
                if(albumVal != null){
                    _album.postValue(albumVal)
                }else{
                    Timber.i("AlbumScreenViewModel-> Response albumVal value is null")
                }
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
}