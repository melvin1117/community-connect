package com.su.communityconnect.model.service

import com.su.communityconnect.model.Category

interface CategoryService {
    suspend fun getCategories(): List<Category>
}
