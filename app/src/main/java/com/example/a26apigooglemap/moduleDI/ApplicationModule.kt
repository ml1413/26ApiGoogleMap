package com.example.a26apigooglemap.moduleDI

import android.content.Context
import android.widget.Toast
import com.example.a26apigooglemap.GAccount
import com.example.a26apigooglemap.Request.ApiClient
import com.example.a26apigooglemap.Request.Repository
import com.example.a26apigooglemap.fragment.SearchFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun getApiClient(): ApiClient {
        return ApiClient()
    }

    @Provides
    @Singleton
    fun getRepository(apiClient: ApiClient): Repository {
        return Repository(client = apiClient)
    }

    @Provides
    @Singleton
    fun getGAccount(@ApplicationContext context: Context): GAccount {
        return GAccount(context)
    }

}