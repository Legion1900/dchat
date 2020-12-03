package com.legion1900.dchat.view.main.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import com.legion1900.dchat.domain.chat.*
import com.legion1900.dchat.domain.chat.usecase.*
import com.legion1900.dchat.domain.contact.AddContactUseCase
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.contact.FindContactUseCase
import com.legion1900.dchat.domain.contact.LoadAvatarsUseCase
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.view.auth.signup.createmnemonic.CreateMnemonicViewModel
import com.legion1900.dchat.view.auth.signup.createprofile.CreateProfileViewModel
import com.legion1900.dchat.view.chat.addcontact.AddContactViewModel
import com.legion1900.dchat.view.chat.chatlist.ChatListViewModel
import com.legion1900.dchat.view.chat.messagelist.MessageListViewModel
import com.legion1900.dchat.view.chat.newchat.createchat.CreateChatViewModel
import com.legion1900.dchat.view.chat.newchat.selectmembers.SelectMembersViewModel
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
        CreateChatUseCase::class to createChatUcProvider(
            { chatContainer.resolve(ChatRepo::class)!! },
            { chatContainer.resolve(AclManager::class)!! }
        ),
        SetChatAvatarUseCase::class to setChatAvatarUcProvider(
            { activityContainer.resolve(TmpFileRepo::class)!! },
            { chatContainer.resolve(ChatManager::class)!! }
        ),
        GetChatsUseCase::class to getChatUcProvider(
            { chatContainer.resolve(ChatRepo::class)!! },
            { chatContainer.resolve(ContactManager::class)!! },
            { chatContainer.resolve(PhotoRepo::class)!! }
        ),
        SendMessageUseCase::class to sendMessageUcProvider {
            chatContainer.resolve(MessageManager::class)!!
        },
        GetMessagesUseCase::class to getMessagesUcProvider(
            { chatContainer.resolve(MessageManager::class)!! },
            { chatContainer.resolve(MessageEventBus::class)!! },
            { chatContainer.resolve(ContactManager::class)!! }
        ),

        ViewModelProvider.Factory::class to viewModelFactoryProvider(
            createViewModelProvider()
        )
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
                    { resolve(GetChatsUseCase::class)!! },
                    { chatContainer.resolve(ProfileManager::class)!! },
                    { chatContainer.resolve(PhotoRepo::class)!! }
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
            CreateChatViewModel::class.java -> getPair {
                createChatVmProvider(
                    { resolve(CreateChatUseCase::class)!! },
                    { resolve(SetChatAvatarUseCase::class)!! }
                )
            }
            MessageListViewModel::class.java -> getPair {
                messageListVmProvider(
                    { resolve(SendMessageUseCase::class)!! },
                    { resolve(GetMessagesUseCase::class)!! }
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
