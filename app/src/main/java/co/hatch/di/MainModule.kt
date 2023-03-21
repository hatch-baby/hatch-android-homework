package co.hatch.di

import co.hatch.HatchDispatchers
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Singleton
    @Provides
    fun provideConnectivityClient(): ConnectivityClient {
        return ConnectivityClient.Factory.create()
    }

    @Singleton
    @Provides
    fun provideHatchDispatchers(): HatchDispatchers {
        return HatchDispatchers()
    }

}
