package hk.hku.cs.hkufriendhub

import org.json.JSONObject
import java.io.Serializable

data class ChatModel (
    val content: String,
    val sender: JSONObject,
    val time: String,
): Serializable