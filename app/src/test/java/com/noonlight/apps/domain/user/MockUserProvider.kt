package com.noonlight.apps.domain.user

class MockUserProvider(
    override val name: String = "Test McTestFace",
    override val phone: String = "11001112222",
    override val pin: String? = "4250"
) : UserProvider