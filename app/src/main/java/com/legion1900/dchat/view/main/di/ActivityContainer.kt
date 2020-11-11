package com.legion1900.dchat.view.main.di

import com.legion1900.dchat.view.main.navigation.DirectionProvider

class ActivityContainer {
    private var directionProvider: DirectionProvider? = null
    private var appContainer: AppContainer? = null

    fun directionProvider(create: () -> DirectionProvider) {
        directionProvider = create()
    }

    fun appContainer(create: () -> AppContainer) {
        appContainer = create()
    }

    fun getAppContainer(): AppContainer = appContainer!!
    fun getDirectionProvider(): DirectionProvider = directionProvider!!
}

fun activityContainer(init: ActivityContainer.() -> Unit): ActivityContainer {
    val container = ActivityContainer()
    container.init()
    return container
}
