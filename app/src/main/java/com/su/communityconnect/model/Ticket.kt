package com.su.communityconnect.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.format.DateTimeFormatter

data class Ticket(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val quantity: Int = 1,
    val purchaseDate: LocalDateTime = LocalDateTime.now(),
    val promoCodeApplied: String? = null,
    val discountApplied: Double = 0.0,
    val totalPrice: Double = 0.0,
    val validations: Map<String, Validation> = emptyMap()
)

data class TicketDTO(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val quantity: Int = 1,
    val purchaseDate: String = "",
    val promoCodeApplied: String? = null,
    val discountApplied: Double = 0.0,
    val totalPrice: Double = 0.0,
    val validations: Map<String, ValidationDTO> = emptyMap()
)

data class Validation(
    val validated: Boolean = false,
    val validatedBy: String? = null,
    val validationTimestamp: LocalDateTime? = null
)

data class ValidationDTO(
    val validated: Boolean = false,
    val validatedBy: String? = null,
    val validationTimestamp: String? = null
)

// Extensions for converting between Ticket and TicketDTO
private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

fun TicketDTO.toTicket(): Ticket {
    return Ticket(
        id = id,
        eventId = eventId,
        userId = userId,
        quantity = quantity,
        purchaseDate = java.time.LocalDateTime.parse(purchaseDate, dateFormatter).toKotlinLocalDateTime(),
        promoCodeApplied = promoCodeApplied,
        discountApplied = discountApplied,
        totalPrice = totalPrice,
        validations = validations.mapValues { (_, dto) -> dto.toValidation() }
    )
}

fun Ticket.toTicketDTO(): TicketDTO {
    return TicketDTO(
        id = id,
        eventId = eventId,
        userId = userId,
        quantity = quantity,
        purchaseDate = purchaseDate.toJavaLocalDateTime().format(dateFormatter),
        promoCodeApplied = promoCodeApplied,
        discountApplied = discountApplied,
        totalPrice = totalPrice,
        validations = validations.mapValues { (_, validation) -> validation.toValidationDTO() }
    )
}

fun ValidationDTO.toValidation(): Validation {
    return Validation(
        validated = validated,
        validatedBy = validatedBy,
        validationTimestamp = validationTimestamp?.let { java.time.LocalDateTime.parse(it, dateFormatter).toKotlinLocalDateTime() }
    )
}

fun Validation.toValidationDTO(): ValidationDTO {
    return ValidationDTO(
        validated = validated,
        validatedBy = validatedBy,
        validationTimestamp = validationTimestamp?.toJavaLocalDateTime()?.format(dateFormatter)
    )
}
