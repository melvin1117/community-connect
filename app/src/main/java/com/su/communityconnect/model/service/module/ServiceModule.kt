package com.su.communityconnect.model.service.module

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.su.communityconnect.model.repository.EventRepository
import com.su.communityconnect.model.repository.TicketRepository
import com.su.communityconnect.model.repository.UserRepository
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.impl.AccountServiceImpl
import com.su.communityconnect.model.service.CategoryService
import com.su.communityconnect.model.service.EventService
import com.su.communityconnect.model.service.TicketService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.model.service.impl.CategoryServiceImpl
import com.su.communityconnect.model.service.impl.EventServiceImpl
import com.su.communityconnect.model.service.impl.TicketServiceImpl
import com.su.communityconnect.model.service.impl.UserServiceImpl
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


    @Binds
    @Singleton
    abstract fun bindUserService(impl: UserServiceImpl): UserService

    @Binds
    @Singleton
    abstract fun bindTicketService(impl: TicketServiceImpl): TicketService
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
    fun provideUserRepository(
        firebaseDatabase: FirebaseDatabase,
        firebaseStorage: FirebaseStorage
    ): UserRepository {
        return UserRepository(firebaseDatabase, firebaseStorage)
    }

    @Provides
    @Singleton
    fun provideTicketRepository(
        firebaseDatabase: FirebaseDatabase,
    ): TicketRepository {
        return TicketRepository(firebaseDatabase)
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}