package com.sagauxassignment.di

import com.sagauxassignment.data.GeneralErrorHandlerImpl
import com.sagauxassignment.data.source.remote.AppDataSource
import com.sagauxassignment.data.source.remote.AppDataSourceImpl
import com.sagauxassignment.data.source.repository.AppRepositoryImpl
import com.sagauxassignment.domain.error.ErrorHandler
import com.sagauxassignment.domain.AppRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    abstract fun provideAppDataSource(
        appDataSourceImpl: AppDataSourceImpl
    ) : AppDataSource

    @Binds
    abstract fun provideAppRepository(repoImpl: AppRepositoryImpl): AppRepository

    @Binds
    abstract fun bindsErrorHandler(errorHandler: GeneralErrorHandlerImpl): ErrorHandler
}