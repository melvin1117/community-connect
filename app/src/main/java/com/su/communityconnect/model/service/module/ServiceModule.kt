package com.su.communityconnect.model.service.module

import com.su.communityconnect.model.service.AccountService
//import com.su.communityconnect.app.model.service.StorageService
import com.su.communityconnect.model.service.impl.AccountServiceImpl
//import com.su.communityconnect.app.model.service.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

//    @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService
}
