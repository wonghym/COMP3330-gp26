package hk.hku.cs.hkufriendhub

import java.io.Serializable

data class ChatroomModel (
    val id: String,
    val name: String,
    val notificationCount: Int,
    val users: ArrayList<String>
): Serializable