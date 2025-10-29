package com.example.words

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.words.ui.AppNav

class MainActivity : ComponentActivity() {

    // нэг газар зарлаад давхцуулж ашиглах ID-нууд
    private val CHANNEL_ID = "words_reminder_channel"
    private val REMINDER_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Notification суваг (Android 8+ дээр заавал хэрэгтэй)
        createNotificationChannel()

        // 2. 24 цаг тутам сануулах Alarm бүртгэх
        scheduleDailyReminder()

        // 3. Compose UI асаах
        setContent {
            AppNav()
        }
    }

    /**
     * Notification Channel үүсгэх
     * Android 8.0 (API 26) ба түүнээс дээш notification ажиллахын тулд channel зайлшгүй хэрэгтэй
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Words reminder"
            val desc = "24 цаг тутам үгээ давтах сануулга"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = desc
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * AlarmManager ашиглаад 24 цаг тутамд Broadcast илгээнэ.
     * Тэр Broadcast-ийг ReminderReceiver хүлээж аваад notification push хийнэ.
     */
    private fun scheduleDailyReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // ReminderReceiver рүү явах интент
        val intent = Intent(this, ReminderReceiver::class.java)

        // PendingIntent: OS энэ Intent-ийг ирээдүйд (24 цаг тутам) гүйцэтгэнэ
        val pending = PendingIntent.getBroadcast(
            this,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 24 цаг миллисекундэд
        val intervalMs = 24L * 60L * 60L * 1000L
        // Анхны trigger: яг одооноос хойш 24 цаг гэж тавьж байна
        val firstTriggerAt = System.currentTimeMillis() + intervalMs

        // Албан шаардлага: "аппаас гарснаас хойш 24 цаг тутам мэдэгдэл"
        // setInexactRepeating нь батарейд ээлтэй, ойролцоогоор давтана
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            firstTriggerAt,
            intervalMs,
            pending
        )

        /**
         * Хэрвээ энэ сануулгыг турших гэж байгаа бол
         * дээрх intervalMs/firstTriggerAt-г жижиг болгож түр туршиж болно:
         *
         * val intervalMs = 60_000L        // 1 минут
         * val firstTriggerAt = System.currentTimeMillis() + 10_000L // 10 секундийн дараа эхний мэдэгдэл
         *
         * Туршилт дууссаныхаа дараа буцаагаад 24 цаг болгож тавьж болно.
         */
    }
}
