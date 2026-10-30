package id.co.asiaraya.hris

import android.app.Application
import id.co.asiaraya.hris.core.data.HrisRepository
import id.co.asiaraya.hris.core.network.ApiClient
import id.co.asiaraya.hris.core.prefs.UserSessionStore

class HrisApplication : Application() {

    lateinit var sessionStore: UserSessionStore
        private set

    lateinit var apiClient: ApiClient
        private set

    lateinit var repository: HrisRepository
        private set

    override fun onCreate() {
        super.onCreate()
        sessionStore = UserSessionStore(this)
        apiClient = ApiClient(sessionStore)
        repository = HrisRepository(this, sessionStore, apiClient)
    }
}
