package com.stockary.common.ui.new_product

import androidx.compose.ui.text.input.TextFieldValue
import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.model.Category
import com.stockary.common.repository.customer.model.Role

object NewProductContract {
    data class State(
        val loading: Boolean = false,
        val response: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val categoryList: Cached<List<Category>> = Cached.NotLoaded(),
        val customerType: Cached<List<Role>> = Cached.NotLoaded(),
        val productName: TextFieldValue = TextFieldValue(),
        val productDescription: TextFieldValue = TextFieldValue(),
        val basePrice: TextFieldValue = TextFieldValue(),
        val prices: Map<String, TextFieldValue> = mapOf()
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()


        data class NameChanged(val newValue: TextFieldValue) : Inputs()
        data class DescriptionChanged(val newValue: TextFieldValue) : Inputs()
        data class PriceChanged(val newValue: TextFieldValue) : Inputs()
        data class PricesChanged(val role: Role, val newValue: TextFieldValue) : Inputs()

        data class Save(val title: String, val description: String) : Inputs()
        data class SaveAndContinue(val prices: List<String>, val category: Category, val types: List<Role>) : Inputs()
        data class UpdateSaveResponse(val response: SupabaseResource<Boolean>, val isContinue: Boolean = false) :
            Inputs()

        data class FetchCategories(val forceRefresh: Boolean) : Inputs()
        data class UpdateCategories(val categoryList: Cached<List<Category>>) : Inputs()
        data class FetchCustomerTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerTypes(val customerTypes: Cached<List<Role>>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
