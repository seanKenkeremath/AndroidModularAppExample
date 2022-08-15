package sean.k.common

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sean.k.common.data.LoginRepository
import sean.k.common.data.LoginRepositoryImpl
import sean.k.login.data.LoginApi
import sean.k.login.data.MockLoginApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    @Singleton
    fun providesLoginApi(): LoginApi {
        return MockLoginApi()
    }

    @Provides
    @Singleton
    fun providesLoginRepository(@ApplicationContext applicationContext: Context, loginApi: LoginApi): LoginRepository {
        return LoginRepositoryImpl(applicationContext, loginApi)
    }
}