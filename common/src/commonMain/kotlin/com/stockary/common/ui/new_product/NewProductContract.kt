package com.stockary.common.ui.new_product

import androidx.compose.ui.text.input.TextFieldValue
import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.form_builder.*
import com.stockary.common.repository.category.model.Category
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.Media
import com.stockary.common.repository.product.model.Product
import com.stockary.common.repository.product.model.UnitType
import java.io.File

object NewProductContract {
    data class State(
        val loading: Boolean = false,
        val response: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val uploadResponse: SupabaseResource<Media> = SupabaseResource.Idle,

        val categoryList: Cached<List<Category>> = Cached.NotLoaded(),
        val customerType: Cached<List<Role>> = Cached.NotLoaded(),
        val unitTypes: Cached<List<UnitType>> = Cached.NotLoaded(),

        val productName: TextFieldValue = TextFieldValue(),
        val productDescription: TextFieldValue = TextFieldValue(),
        val productId: String? = null,
        val product: SupabaseResource<Product> = SupabaseResource.Idle,
        val formState: FormState<BaseState<*>> = FormState(
            fields = listOf(
                TextFieldState(
                    name = Product::title.name,
                    validators = listOf(Validators.Required()),
                ),
                TextFieldState(
                    name = Product::code.name,
                    validators = listOf(),
                ),
                TextFieldState(
                    name = "photo",
                    validators = listOf(),
                    transform = {
                        it
                    }
                ),
                TextFieldState(name = Product::unitAmount.name,
                    validators = listOf(
                        Validators.Required()
                    ),
                    transform = {
                        it.toFloatOrNull() ?: 0f
                    }
                ),
                ChoiceState(
                    name = Product::category.name, validators = listOf(
                        Validators.Required()
                    )
                ),
                ChoiceState(
                    name = Product::unitType.name, validators = listOf(
                        Validators.Required()
                    )
                )
            )
        )
    )

    sealed class Inputs {
        data class Initialize(val productId: String? = null) : Inputs()
        object GoBack : Inputs()
        object Update : Inputs()

        data class Save(val title: String, val description: String) : Inputs()
        object SaveAndContinue : Inputs()
        object UpdateForm : Inputs()

        data class UpdateSaveResponse(val response: SupabaseResource<Boolean>, val isContinue: Boolean = false) :
            Inputs()

        data class UploadPhoto(val file: File) : Inputs()
        data class UpdateUploadResponse(val uploadResponse: SupabaseResource<Media>) : Inputs()

        data class FetchCategories(val forceRefresh: Boolean) : Inputs()
        data class FetchUnitTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCategories(val categoryList: Cached<List<Category>>) : Inputs()
        data class UpdateUnitTypes(val unitTypes: Cached<List<UnitType>>) : Inputs()
        data class FetchCustomerTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerTypes(val customerTypes: Cached<List<Role>>) : Inputs()

        data class GetProduct(val productId: String) : Inputs()
        data class UpdateProduct(val product: SupabaseResource<Product>) : Inputs()

    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
