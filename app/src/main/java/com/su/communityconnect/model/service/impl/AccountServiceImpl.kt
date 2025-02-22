package com.su.communityconnect.model.service.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.su.communityconnect.model.User
import com.su.communityconnect.model.service.AccountService
import com.su.communityconnect.model.service.UserService
import com.su.communityconnect.model.state.UserState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val userService: UserService
) : AccountService {

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser.toCommunityConnectUser())
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override fun getUserProfile(): User {
        return Firebase.auth.currentUser.toCommunityConnectUser()
    }

    override suspend fun updateDisplayName(newDisplayName: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newDisplayName
        }

        Firebase.auth.currentUser!!.updateProfile(profileUpdates).await()
    }

    override suspend fun linkAccountWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.currentUser!!.linkWithCredential(firebaseCredential).await()
    }

    override suspend fun linkAccountWithEmail(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        Firebase.auth.currentUser!!.linkWithCredential(credential).await()
    }

    override suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val result = Firebase.auth.signInWithCredential(firebaseCredential).await()
        val firebaseUser = result.user ?: return

        // Fetch user data from the database
        val dbUser = userService.getUser(firebaseUser.uid)

        if (dbUser == null) {
            val userToSave = firebaseUser.toCommunityConnectUser()
            userService.saveUser(userToSave)
            UserState.updateUser(userToSave)
        } else {
            UserState.updateUser(dbUser)
        }

    }

    override suspend fun signInWithEmail(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
        val dbUser = userService.getUser(currentUserId)

        if (dbUser != null) {
            UserState.updateUser(dbUser)
        }
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        Firebase.auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun sendEmailVerification() {
        Firebase.auth.currentUser?.sendEmailVerification()?.await()
    }

    override suspend fun isEmailVerified(): Boolean {
        Firebase.auth.currentUser?.reload()?.await() // Ensure the latest user state
        return Firebase.auth.currentUser?.isEmailVerified ?: false
    }

    private fun FirebaseUser?.toCommunityConnectUser(): User {
        return if (this == null) User() else User(
            id = this.uid,
            email = this.email ?: "",
            provider = this.providerData.firstOrNull()?.providerId ?: "",
            displayName = this.displayName ?: "",
            profilePictureUrl = this.photoUrl?.toString() ?: "",
            phone = this.phoneNumber ?: ""
        )
    }
}
