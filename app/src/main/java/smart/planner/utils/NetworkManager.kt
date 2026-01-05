package smart.planner.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * NetworkManager - Quản lý trạng thái kết nối mạng
 *
 * Features:
 * - isOnline(): Check trạng thái mạng hiện tại
 * - networkState: LiveData để observe thay đổi
 * - Tự động update khi có/mất mạng
 */
class NetworkManager(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // LiveData để observe trạng thái mạng
    private val _networkState = MutableLiveData<Boolean>()
    val networkState: LiveData<Boolean> = _networkState

    // Network callback để lắng nghe thay đổi
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // Có mạng
            _networkState.postValue(true)
        }

        override fun onLost(network: Network) {
            // Mất mạng
            _networkState.postValue(false)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            // Thay đổi khả năng mạng (WiFi ↔ Mobile)
            val hasInternet = networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
            _networkState.postValue(hasInternet)
        }
    }

    init {
        // Đăng ký lắng nghe thay đổi mạng
        registerNetworkCallback()

        // Set trạng thái ban đầu
        _networkState.value = isOnline()
    }

    /**
     * Check trạng thái mạng hiện tại
     * @return true nếu có mạng, false nếu không
     */
    fun isOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Đăng ký callback để lắng nghe thay đổi mạng
     */
    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    /**
     * Hủy đăng ký callback (gọi khi không dùng nữa)
     */
    fun unregisterCallback() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Check loại mạng đang dùng
     * @return "WiFi", "Mobile", "None"
     */
    fun getNetworkType(): String {
        val network = connectivityManager.activeNetwork ?: return "None"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "None"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Unknown"
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: NetworkManager? = null

        /**
         * Singleton instance
         */
        fun getInstance(context: Context): NetworkManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}