package com.samyak.smalitm.adapters

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * The TokenAdapter tries to push bearerToken field during deserialization of the json
 */
class TokenAdapter(private val bearerToken: String) : TypeAdapterFactory {

    override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, typeToken)

        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T) {
                delegate.write(out, value)
            }

            override fun read(`in`: JsonReader): T {
                val json = JsonParser.parseReader(`in`)

                if (json.isJsonObject) {
                    val jsonObject = json.asJsonObject
                    if (!jsonObject.has("bearerToken")) {
                        jsonObject.addProperty("bearerToken", bearerToken)
                    }
                }

                return delegate.fromJsonTree(json)
            }
        }
    }
}
