package com.legion1900.dchat.view.main.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.contact.AddContactUseCase
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.contact.FindContactUseCase
import com.legion1900.dchat.domain.contact.LoadAvatarsUseCase
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.view.auth.signup.createmnemonic.CreateMnemonicViewModel
import com.legion1900.dchat.view.auth.signup.createprofile.CreateProfileViewModel
import com.legion1900.dchat.view.chat.addcontact.AddContactViewModel
import com.legion1900.dchat.view.chat.chatlist.ChatListViewModel
import com.legion1900.dchat.view.chat.newchat.SelectMembersViewModel
import com.legion1900.dchat.view.main.di.providers.*
import kotlin.reflect.KClass

class FragmentContainer(
    private val activityContainer: ActivityContainer,
    private val vmClass: Class<out ViewModel>,
    private val chatContainer: ChatContainer
) : Container {

    private val dependencyProvider = DependencyProvider(
        MnemonicGenerator::class to mnemonicGeneratorProvider(),
        FindContactUseCase::class to findContactUcProvider {
            chatContainer.resolve(ContactManager::class)!!
        },
        AddContactUseCase::class to addContactUcProvider {
            chatContainer.resolve(ContactManager::class)!!
        },
        LoadAvatarsUseCase::class to loadAvatarsUcProvider {
            chatContainer.resolve(PhotoRepo::class)!!
        },

        ViewModelProvider.Factory::class to viewModelFactoryProvider(createViewModelProvider())
    )

    override fun <T : Any> resolve(klass: KClass<T>): T? {
        var dep = dependencyProvider[klass]
        if (dep == null) dep = chatContainer.resolve(klass)
        if (dep == null) dep = activityContainer.resolve(klass)
        return dep
    }

    private fun createViewModelProvider(): Pair<Class<out ViewModel>, Provider<out ViewModel>> {
        return when (vmClass) {
            CreateMnemonicViewModel::class.java -> getPair {
                createMnemonicVmProvider { dependencyProvider[MnemonicGenerator::class]!! }
            }
            CreateProfileViewModel::class.java -> getPair {
                createProfileVmProvider(
                    { chatContainer.resolve(RegistrationManager::class)!! },
                    { activityContainer.resolve(AppStateRepo::class)!! },
                    { activityContainer.resolve(TmpFileRepo::class)!! },
                    { chatContainer.resolve(ProfileManager::class)!! }
                )
            }
            ChatListViewModel::class.java -> getPair {
                chatListVmProvider(
                    { chatContainer.resolve(ChatRepo::class)!! },
                    { chatContainer.resolve(ProfileManager::class)!! }
                )
            }
            AddContactViewModel::class.java -> getPair {
                addContactVmProvider(
                    { resolve(FindContactUseCase::class)!! },
                    { resolve(LoadAvatarsUseCase::class)!! },
                    { resolve(AddContactUseCase::class)!! }
                )
            }
            SelectMembersViewModel::class.java -> getPair {
                selectMembersVmProvider(
                    { chatContainer.resolve(ContactManager::class)!! },
                    { resolve(LoadAvatarsUseCase::class)!! }
                )
            }
            else -> throw Exception("Can not create requested ViewModel ${vmClass.name}")
        }
    }

    private inline fun <reified T : ViewModel> getPair(
        crossinline newVmProvider: () -> Provider<T>
    ): Pair<Class<T>, Provider<T>> {
        return T::class.java to newVmProvider()
    }
}
