package com.example.demo

import demo.v1.UserServiceGrpcKt
import demo.v1.getUserRequest
import demo.v1.setUserRequest
import io.grpc.ManagedChannel
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import io.grpc.util.MutableHandlerRegistry
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking

class ApplicationTest : DatabaseSpec() {

    private val grpcCleanupRule: GrpcCleanupRule = GrpcCleanupRule()

    private lateinit var channel: ManagedChannel
    private lateinit var serviceRegistry: MutableHandlerRegistry

    @BeforeEach
    fun setup() {
        dbSetup(Users)

        // Set up a grpc server that we can test the UserProfileServer against
        val userServiceName: String = InProcessServerBuilder.generateName()

        serviceRegistry = MutableHandlerRegistry()

        grpcCleanupRule.register(
            InProcessServerBuilder.forName(userServiceName)
                .fallbackHandlerRegistry(serviceRegistry)
                .build()
                .start()
        )
        channel = grpcCleanupRule.register(
            InProcessChannelBuilder.forName(userServiceName).build()
        )
    }

    @AfterEach
    fun tearDown() {
        dbCleanup(Users)
    }

    @Test
    fun `test setting and getting a user`() = runBlocking {
        // Stand up our service stubs
        val userStub = UserServiceGrpcKt.UserServiceCoroutineStub(channel)
        val testUser = demo.v1.user { name = "terezi"; about = "W3 M4K3 OUR OWN LUCK 4ND YOU'R3 4BOUT TO PROV3 TH4T" }
        println("test")
        // Set user should return what was set
        val reply1 = userStub.setUser(setUserRequest { user = testUser })
        reply1.user.name shouldBe testUser.name
        reply1.user.about shouldBe testUser.about

        val reply2 = userStub.getUser(getUserRequest { name = "terezi" })
        reply2.user shouldBe reply1.user
    }
}
