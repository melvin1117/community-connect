package com.su.communityconnect.model.service.impl

import com.su.communityconnect.model.Category
import com.su.communityconnect.model.repository.CategoryRepository
import com.su.communityconnect.model.service.CategoryService
import javax.inject.Inject

class CategoryServiceImpl @Inject constructor(
    private val repository: CategoryRepository
) : CategoryService {
    override suspend fun getCategories(): List<Category> {
        return repository.fetchCategories()
    }
}
