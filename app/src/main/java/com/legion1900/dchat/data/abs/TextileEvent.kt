package com.legion1900.dchat.data.abs

sealed class TextileEvent

data class NodeStateChanged(val isRunning: Boolean) : TextileEvent()
