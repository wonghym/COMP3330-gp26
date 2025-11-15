package hk.hku.cs.hkufriendhub

import java.io.Serializable

data class PostModel (
    val id: String,
    val userId: String,
    val name: String,
    val timestamp: String,
    val title: String,
    val text: String,
    val hashtags: ArrayList<String>,
    val groupStat: String,
    val isJoined: Boolean,
    val profilePic: String?,
) : Serializable