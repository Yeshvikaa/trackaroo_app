package com.simats.trackaroo

/**
 * Simple model representing one chat message.
 * @param text the message text
 * @param isUser true for driver's message (right-side bubble), false for bot (left-side)
 * @param timestamp optional, useful for storing/displaying time
 */
data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)