package com.education.creditscore.calculator.di

import android.content.Context
import androidx.room.Room
import com.education.creditscore.calculator.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CreditProDatabase =
        Room.databaseBuilder(context, CreditProDatabase::class.java, "creditpro.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(db: CreditProDatabase) = db.userDao()

    @Provides
    fun provideCreditScoreDao(db: CreditProDatabase) = db.creditScoreDao()

    @Provides
    @Singleton
    fun providePrefsRepository(@ApplicationContext context: Context) =
        PreferencesRepository(context)

    @Provides
    @Singleton
    fun provideRepository(
        userDao: UserDao,
        creditScoreDao: CreditScoreDao,
        prefs: PreferencesRepository,
        @ApplicationContext context: Context
    ) = CreditProRepository(userDao, creditScoreDao, prefs, context)
}
