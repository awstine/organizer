package com.organizethem.organizethem.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.organizethem.organizethem.domain.User

fun FirebaseUser.toUser() = User(
    uid = uid,
    email = email?: "",
    displayName = displayName
)