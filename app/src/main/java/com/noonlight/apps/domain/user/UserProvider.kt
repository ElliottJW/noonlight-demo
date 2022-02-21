package com.noonlight.apps.domain.user

/**
 * Describes a logged-in user.
 */
interface UserProvider {

    // TODO: Add more User properties?

    val name: String
    val phone: String
    val pin: String?
}