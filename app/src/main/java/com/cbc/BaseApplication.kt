package com.cbc

import android.app.Application
import com.cbc.data.Apis
import com.cbc.data.repositories.ChatRepository
import com.cbc.data.NetworkConnectionInterceptor
import com.cbc.data.repositories.GroupChatRepository
import com.cbc.localdb.AppDatabase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class BaseApplication: Application() , KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@BaseApplication))

        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { Apis(instance()) }
        bind() from singleton { ChatRepository(instance(), instance()) }
        bind() from singleton { GroupChatRepository(instance(), instance()) }
    }

}