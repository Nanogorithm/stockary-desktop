package com.stockary.common.repository.product

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.Media
import com.stockary.common.repository.product.model.Product
import com.stockary.common.repository.product.model.UnitType
import java.io.File

object ProductRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val dataListInitialized: Boolean = false,
        val productList: Cached<List<Product>> = Cached.NotLoaded(),

        val customerTypesInitialized: Boolean = false,
        val customerTypes: Cached<List<Role>> = Cached.NotLoaded(),

        val unitTypes: Cached<List<UnitType>> = Cached.NotLoaded(),
        val mediaUploadResponse: SupabaseResource<Media> = SupabaseResource.Idle,
        val product: SupabaseResource<Product> = SupabaseResource.Idle,

        val saving: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val updating: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val deleting: SupabaseResource<Boolean> = SupabaseResource.Idle
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshProductList(val forceRefresh: Boolean) : Inputs()
        data class ProductListUpdated(val dataList: Cached<List<Product>>) : Inputs()

        data class RefreshUnitTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateUnitTypes(val unitTypes: Cached<List<UnitType>>) : Inputs()

        data class RefreshCustomerTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerTypes(val roles: Cached<List<Role>>) : Inputs()

        data class UploadPhoto(val file: File) : Inputs()
        data class UpdateUploadResponse(val mediaUploadResponse: SupabaseResource<Media>) : Inputs()


        data class GetProduct(val productId: String) : Inputs()
        data class GetPhotoUrl(val path: String) : Inputs()

        data class Add(val product: Product, val prices: List<Float>, val types: List<Role>, val media: Media?) :
            Inputs()

        data class Edit(
            val product: Product,
            val updated: Product,
            val prices: List<Float>,
            val types: List<Role>,
            val photo: Media?
        ) : Inputs()

        data class Delete(val product: Product) : Inputs()
    }
}
