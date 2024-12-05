package com.su.communityconnect.model.service.module

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.su.communityconnect.model.repository.EventRepository
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.impl.AccountServiceImpl
import com.su.communityconnect.model.service.CategoryService
import com.su.communityconnect.model.service.EventService
import com.su.communityconnect.model.service.impl.CategoryServiceImpl
import com.su.communityconnect.model.service.impl.EventServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    @Singleton
    abstract fun bindCategoryService(impl: CategoryServiceImpl): CategoryService

    @Binds
    @Singleton
    abstract fun bindEventService(impl: EventServiceImpl): EventService
}


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideEventRepository(
        firebaseDatabase: FirebaseDatabase,
        firebaseStorage: FirebaseStorage
    ): EventRepository {
        return EventRepository(firebaseDatabase, firebaseStorage)
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}