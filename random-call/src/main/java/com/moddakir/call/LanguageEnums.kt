package com.moddakir.call

import com.moddakir.call.utils.Locales

enum class Language(val code: String) {

    english("English"),
    arabic("العربية"),
    indonesia("Bahasa Indonesia"),
    urdu("اُرْدُوْ"),
    french("Français"),
    empty(""),;


}

fun String.getAppLanguage(): Boolean {
    return when (this) {
        Locales.Arabic.language -> true
        Locales.English.language -> true
        Locales.Indonesian.language -> true
        Locales.Urdu.language -> true
        Locales.French.language -> true
        else -> {
            false
        }
    }

}

