package com.desynatdealer.utils.translation

data class TranslateResponse(
    val data: Data
) {
    data class Data(
        val translations: List<Translation>
    )

    data class Translation(
        val translatedText: String
    )
}