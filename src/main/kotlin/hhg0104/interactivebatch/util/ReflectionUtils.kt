package hhg0104.interactivebatch.util

import java.lang.reflect.Field
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

class ReflectionUtils {

    companion object {

        fun toString(obj: Any): String? {
            if (obj == null) return null
            val dataMap = toMap(obj) ?: return null

            val stringJoiner = StringJoiner(", ")

            dataMap.entries.forEach {
                stringJoiner.add("${it.key}: ${it.value}")
            }

            return stringJoiner.toString()
        }

        fun toMap(obj: Any, filter: ((Field) -> Boolean)? = null): Map<String, String>? {

            if (obj == null) return null

            val dataMap = mutableMapOf<String, String>()

            obj.javaClass.declaredFields.forEach { field ->
                if (filter != null && !filter(field)) return@forEach

                field.isAccessible = true

                var value = field.get(obj)
                if (value != null && field.type.isAssignableFrom(LocalDateTime::class.java)) {
                    value = LocalDateUtil.format(value as LocalDateTime?, LocalDateUtil.DATETIME_FORMAT)
                }

                dataMap[field.name] = value.toString()
            }

            return dataMap
        }
    }
}