package com.desynatdealer.utils.translation

object ChatLanguageDetector {

    fun isArabic(text: String): Boolean {
        return text.any { char ->
            char in '\u0600'..'\u06FF' ||   // Arabic
                    char in '\u0750'..'\u077F' ||   // Arabic Supplement
                    char in '\u08A0'..'\u08FF'      // Arabic Extended
        }
    }
}