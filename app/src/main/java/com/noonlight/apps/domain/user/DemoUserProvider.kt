package com.noonlight.apps.domain.user

class DemoUserProvider(
    override val name: String = "Elliott JW",
    override val phone: String = "11009998888",
    override val pin: String? = "4242"
) : UserProvider