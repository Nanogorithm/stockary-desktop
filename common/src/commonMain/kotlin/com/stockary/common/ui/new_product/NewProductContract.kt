package com.stockary.common.ui.new_product

import androidx.compose.ui.text.input.TextFieldValue
import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.form_builder.*
import com.stockary.common.repository.category.model.Category
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.Product
import com.stockary.common.repository.product.model.UnitType
import java.io.File

object NewProductContract {
    data class State(
        val loading: Boolean = false,
        val response: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val uploadResponse: SupabaseResource<String> = SupabaseResource.Idle,

        val categoryList: Cached<List<Category>> = Cached.NotLoaded(),
        val customerType: Cached<List<Role>> = Cached.NotLoaded(),
        val unitTypes: Cached<List<UnitType>> = Cached.NotLoaded(),

        val productName: TextFieldValue = TextFieldValue(),
        val productDescription: TextFieldValue = TextFieldValue(),
        val basePrice: TextFieldValue = TextFieldValue(),
        val formState: FormState<BaseState<*>> = FormState(
            fields = listOf(
                TextFieldState(
                    name = Product::title.name,
                    validators = listOf(Validators.Required()),
                ), TextFieldState(
                    name = Product::description.name,
                    validators = listOf(Validators.Required()),
                ), TextFieldState(
                    name = "photo",
                    validators = listOf(),
                ), TextFieldState(name = "unit_amount", validators = listOf(
                    Validators.Required()
                ), transform = {
                    it.toFloatOrNull() ?: 0f
                }), ChoiceState(name = "category_id", validators = listOf(
                    Validators.Required()
                ), transform = {
                    it.toInt()
                }), ChoiceState(name = "unit_type_id", validators = listOf(
                    Validators.Required()
                ), transform = { it.toInt() })
            )
        )
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()

        data class Save(val title: String, val description: String) : Inputs()
        object SaveAndContinue : Inputs()

        data class UpdateSaveResponse(val response: SupabaseResource<Boolean>, val isContinue: Boolean = false) :
            Inputs()

        data class UploadPhoto(val file: File) : Inputs()
        data class UpdateUploadResponse(val uploadResponse: SupabaseResource<String>) : Inputs()

        data class FetchCategories(val forceRefresh: Boolean) : Inputs()
        data class FetchUnitTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCategories(val categoryList: Cached<List<Category>>) : Inputs()
        data class UpdateUnitTypes(val unitTypes: Cached<List<UnitType>>) : Inputs()
        data class FetchCustomerTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerTypes(val customerTypes: Cached<List<Role>>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
