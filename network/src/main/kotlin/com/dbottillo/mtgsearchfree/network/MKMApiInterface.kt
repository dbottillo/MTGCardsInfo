package com.dbottillo.mtgsearchfree.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MKMApiInterface {
    @GET("ws/v2.0/output.json/products/find")
    fun findProduct(
        @Query("search") productName: String
    ): Single<MKMProductsApi>

    @GET("ws/v2.0/output.json/products/{id}")
    fun findProduct(@Path("id") id: Long): Single<MKMSingleProductApi>
}

class MKMProductsApi(
    val product: List<MKMMultipleProductApi>?
)

class MKMMultipleProductApi(
    val idProduct: Long?,
    val expansionName: String?
)

class MKMSingleProductApi(
    val product: MKMProductApi?
)

class MKMProductApi(
    val website: String?,
    val priceGuide: MKMPriceGuideApi?
)

class MKMPriceGuideApi(
    val SELL: Double?,
    val LOW: Double?,
    val TREND: Double?
)

/*
{
    product: {
    idProduct:                  // Product ID
    idMetaproduct:              // Metaproduct ID
    countReprints:              // Number of total products bundled by the metaproduct
    enName:                     // Product's English name
    localization: {}            // localization entities for the product's name
    category: {                 // Category entity the product belongs to
        idCategory:             // Category ID
        categoryName:           // Category's name
    }
    website:                    // URL to the product (relative to MKM's base URL)
    image:                      // Path to the product's image
    gameName:                   // the game's name
    categoryName:               // the category's name
    number:                     // Number of product within the expansion (where applicable)
    rarity:                     // Rarity of product (where applicable)
    expansionName:              // Expansion's name
    links: {}                   // HATEOAS links
    *//* The following information is only returned for responses that return the detailed product entity *//*
    expansion: {                // detailed expansion information (where applicable)
        idExpansion:
        enName:
        expansionIcon:
    }
    priceGuide: {               // Price guide entity '''(ATTN: not returned for expansion requests)'''
        SELL:                   // Average price of articles ever sold of this product
        LOW:                    // Current lowest non-foil price (all conditions)
        LOWEX+:                 // Current lowest non-foil price (condition EX and better)
        LOWFOIL:                // Current lowest foil price
        AVG:                    // Current average non-foil price of all available articles of this product
        TREND:                  // Current trend price
        TRENDFOIL:              // Current foil trend price
    }
    reprint: [                  // Reprint entities for each similar product bundled by the metaproduct
    {
        idProduct:
        expansion:
        expIcon:
    }
    ]
}
}*/
