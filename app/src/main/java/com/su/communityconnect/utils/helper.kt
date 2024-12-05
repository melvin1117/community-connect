package com.su.communityconnect.utils

import android.net.Uri

fun convertUriListToStringList(uriList: List<Uri>): List<String> {
    return uriList.map { it.toString() }
}