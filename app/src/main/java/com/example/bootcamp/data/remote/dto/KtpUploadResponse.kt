package com.example.bootcamp.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Response DTO for KTP upload endpoint. */
data class KtpUploadResponse(@SerializedName("ktpPath") val ktpPath: String)
