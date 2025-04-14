package com.example.autoclickerapp.di
//
//import android.content.Context
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AuthModule {
//    // Firebase Auth
//    @Provides
//    @Singleton
//    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
//
//    // Firestore
//    @Provides
//    @Singleton
//    fun provideFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()
//
//    // Auth Repository
//    @Provides
//    @Singleton
//    fun provideAuthRepository(
//        firebaseAuth: FirebaseAuth,
//        firestore: FirebaseFirestore
//    ): AuthRepository = FirebaseAuthRepositoryImpl(firebaseAuth, firestore)
//}