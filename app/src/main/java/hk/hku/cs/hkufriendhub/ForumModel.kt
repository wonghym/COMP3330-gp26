package hk.hku.cs.hkufriendhub

import java.io.Serializable

data class ForumModel (
    val id: String,
    val name: String,
    val timestamp: String,
    val text: String,
    val like: Int,
)