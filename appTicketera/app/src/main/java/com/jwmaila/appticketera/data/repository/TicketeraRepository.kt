package com.jwmaila.appticketera.data.repository

import com.jwmaila.appticketera.data.local.UserPreferences
import com.jwmaila.appticketera.data.model.*
import com.jwmaila.appticketera.data.remote.TicketeraApi
import com.jwmaila.appticketera.data.remote.getUserMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketeraRepository @Inject constructor(
    private val api: TicketeraApi,
    private val userPreferences: UserPreferences
) {
    // Auth
    suspend fun register(email: String, password: String, firstName: String, lastName: String): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(email, password, firstName, lastName))
            userPreferences.saveAuthToken(response.accessToken)
            userPreferences.saveUserData(
                response.user.id,
                response.user.fullName,
                response.user.email,
                response.user.role.name
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            userPreferences.saveAuthToken(response.accessToken)
            userPreferences.saveUserData(
                response.user.id,
                response.user.fullName,
                response.user.email,
                response.user.role.name
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    fun logout() {
        userPreferences.clear()
    }
    
    fun isLoggedIn(): Boolean = userPreferences.isLoggedIn()
    
    fun getUserRole(): String? = userPreferences.getUserRole()
    
    suspend fun getProfile(): Result<User> {
        return try {
            val user = api.getProfile()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    // Events
    suspend fun getEvents(): Result<List<Event>> {
        return try {
            val events = api.getEvents()
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getEvent(id: String): Result<EventWithSeats> {
        return try {
            val event = api.getEvent(id)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    suspend fun createEvent(request: CreateEventRequest): Result<Event> {
        return try {
            val event = api.createEvent(request)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    suspend fun updateEvent(id: String, request: UpdateEventRequest): Result<Event> {
        return try {
            val event = api.updateEvent(id, request)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    suspend fun deleteEvent(id: String): Result<Unit> {
        return try {
            api.deleteEvent(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    // Tickets
    suspend fun createPaymentIntent(eventId: String, seatIds: List<String>): Result<PaymentIntentResponse> {
        return try {
            val response = api.createPaymentIntent(CreatePaymentIntentRequest(eventId, seatIds))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    suspend fun confirmPayment(paymentIntentId: String, seatIds: List<String>): Result<List<Ticket>> {
        return try {
            val tickets = api.confirmPayment(paymentIntentId, ConfirmPaymentRequest(paymentIntentId, seatIds))
            Result.success(tickets)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    suspend fun getMyTickets(): Result<List<Ticket>> {
        return try {
            val tickets = api.getMyTickets()
            Result.success(tickets)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }

    suspend fun getTicketById(id: String): Result<Ticket> {
        return try {
            val ticket = api.getTicketById(id)
            Result.success(ticket)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }

    suspend fun getAllTickets(): Result<List<Ticket>> {
        return try {
            val tickets = api.getAllTickets()
            Result.success(tickets)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    suspend fun verifyTicket(qrCode: String): Result<VerifyTicketResponse> {
        return try {
            val response = api.verifyTicket(VerifyTicketRequest(qrCode))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
    
    // Users (admin only)
    suspend fun getUsers(): Result<List<User>> {
        return try {
            val users = api.getUsers()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }

    suspend fun createUser(request: CreateUserRequest): Result<User> {
        return try {
            val user = api.createUser(request)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }

    suspend fun updateUser(id: String, request: UpdateUserRequest): Result<User> {
        return try {
            val user = api.updateUser(id, request)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }

    suspend fun deleteUser(id: String): Result<Unit> {
        return try {
            api.deleteUser(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): Result<User> {
        return try {
            val user = api.updateProfile(request)
            userPreferences.saveUserData(user.id, user.fullName, user.email, user.role.name)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(e.getUserMessage()))
        }
    }
}
