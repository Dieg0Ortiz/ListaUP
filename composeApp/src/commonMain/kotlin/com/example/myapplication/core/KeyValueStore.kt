package com.example.myapplication.core

/** Almacenamiento clave-valor que sobrevive a que se cierre la app. */
interface KeyValueStore {
    fun get(key: String): String?
    fun put(key: String, value: String?)
}

expect fun createKeyValueStore(): KeyValueStore

/** Respaldo en memoria para plataformas sin almacenamiento persistente. */
class InMemoryKeyValueStore : KeyValueStore {
    private val values = mutableMapOf<String, String>()
    override fun get(key: String): String? = values[key]
    override fun put(key: String, value: String?) {
        if (value == null) values.remove(key) else values[key] = value
    }
}
