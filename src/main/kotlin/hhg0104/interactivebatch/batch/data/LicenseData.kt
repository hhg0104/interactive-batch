package hhg0104.interactivebatch.batch.data

import java.io.Serializable

class LicenseData : Serializable {

    companion object {
        val mapColumnsToProperties = mapOf(
            "ID" to "id",
            "LICENSE_FRONT_IMAGE_URL" to "licenseFrontImageUrl",
            "LICENSE_BACK_IMAGE_URL" to "licenseBackImageUrl"
        )
    }

    var id: String? = null

    var licenseFrontImageUrl: String? = null

    var licenseBackImageUrl: String? = null
}