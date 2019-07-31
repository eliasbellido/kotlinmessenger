package com.beyondthecode.kotlinmessenger.models

class ChatMessage(val id: String, val text:String, val fromId: String, val toid: String, val timestamp: Long) {
    constructor() : this("","","","",-1)
}
