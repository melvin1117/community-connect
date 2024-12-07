package com.su.communityconnect.utils

import android.content.Intent
import android.net.Uri

fun convertUriListToStringList(uriList: List<Uri>): List<String> {
    return uriList.map { it.toString() }
}

fun openDialer(phone: String, context: android.content.Context) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun openEmailClient(email: String, context: android.content.Context) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}