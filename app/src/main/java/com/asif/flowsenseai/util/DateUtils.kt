package com.asif.flowsenseai.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for date formatting operations.
 * Provides helper functions to convert timestamps to readable strings.
 */
object DateUtils {
    
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault())
    
    /**
     * Format timestamp to a readable string.
     * Shows "Today", "Yesterday", or the date in "dd MMM" format.
     * 
     * @param timestamp The timestamp to format
     * @return Formatted date string (e.g., "Today at 3:45 PM", "Yesterday", "22 Mar")
     */
    fun formatReadableDate(timestamp: Long): String {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance()
        date.timeInMillis = timestamp
        
        // Check if it's today
        if (isSameDay(now, date)) {
            return "Today at ${timeFormat.format(date.time)}"
        }
        
        // Check if it's yesterday
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        if (isSameDay(yesterday, date)) {
            return "Yesterday"
        }
        
        // Check if it's within the last week
        val weekAgo = Calendar.getInstance()
        weekAgo.add(Calendar.DAY_OF_YEAR, -7)
        if (date.after(weekAgo)) {
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(date.time)
            return dayOfWeek
        }
        
        // Otherwise show the date in "dd MMM" format
        return SimpleDateFormat("dd MMM", Locale.getDefault()).format(date.time)
    }
    
    /**
     * Format timestamp to full date string.
     * 
     * @param timestamp The timestamp to format
     * @return Full date string (e.g., "24 Mar 2026")
     */
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to time string.
     * 
     * @param timestamp The timestamp to format
     * @return Time string (e.g., "3:45 PM")
     */
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
    
    /**
     * Format timestamp to date and time string.
     * 
     * @param timestamp The timestamp to format
     * @return Date and time string (e.g., "24 Mar 2026, 3:45 PM")
     */
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }
    
    /**
     * Check if two Calendar instances represent the same day.
     * 
     * @param cal1 First calendar
     * @param cal2 Second calendar
     * @return True if both calendars represent the same day
     */
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    /**
     * Get a relative time string (e.g., "2 hours ago", "3 days ago").
     * 
     * @param timestamp The timestamp to format
     * @return Relative time string
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
            hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
            else -> formatDate(timestamp)
        }
    }
    
    /**
     * Check if a date is today.
     * 
     * @param timestamp The timestamp to check
     * @return True if the timestamp is from today
     */
    fun isToday(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance()
        date.timeInMillis = timestamp
        return isSameDay(now, date)
    }
    
    /**
     * Check if a date is yesterday.
     * 
     * @param timestamp The timestamp to check
     * @return True if the timestamp is from yesterday
     */
    fun isYesterday(timestamp: Long): Boolean {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        val date = Calendar.getInstance()
        date.timeInMillis = timestamp
        return isSameDay(yesterday, date)
    }
}
