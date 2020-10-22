package com.legion1900.dchat.data.textile.abs

sealed class TextileEvent

data class NodeStateChanged(val isRunning: Boolean) : TextileEvent()
