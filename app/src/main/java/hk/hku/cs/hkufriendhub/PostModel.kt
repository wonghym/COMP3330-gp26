package hk.hku.cs.hkufriendhub

import java.io.Serializable

data class PostModel (
    val username: String,
    val timestamp: String,
    val title: String,
    val text: String,
    val hashtags: ArrayList<String>,
    val groupStat: String
) : Serializable