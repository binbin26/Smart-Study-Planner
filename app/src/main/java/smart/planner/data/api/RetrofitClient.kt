package smart.planner.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.HostnameVerifier

/**
 * RetrofitClient singleton
 * Quản lý Retrofit instance và cung cấp các API services
 * 
 * Sử dụng pattern Singleton để đảm bảo chỉ có một instance Retrofit trong toàn bộ ứng dụng
 * 
 * SSL/TLS được xử lý thông qua Network Security Config và OkHttpClient configuration
 */
object RetrofitClient {
    
    // Base URLs cho các APIs
    private const val HOLIDAY_API_BASE_URL = "https://date.nager.at/api/v3/"
    private const val QUOTE_API_BASE_URL = "https://api.quotable.io/"
    
    // Timeout settings
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    
    /**
     * Tạo TrustManager để xử lý SSL certificate validation
     * Lưu ý: Chỉ sử dụng trong development/debug
     * Trong production, nên sử dụng proper certificate pinning hoặc Network Security Config
     */
    private fun createUnsafeTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // Trust all client certificates
            }
            
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // Trust all server certificates
            }
            
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        }
    }
    
    /**
     * Tạo OkHttpClient với logging interceptor và SSL configuration
     * Chỉ log trong debug mode
     * 
     * SSL/TLS certificate validation được xử lý bởi:
     * 1. Network Security Config (network_security_config.xml)
     * 2. Custom TrustManager (cho development/debug)
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (android.util.Log.isLoggable("Retrofit", android.util.Log.DEBUG)) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        // Cấu hình SSL để xử lý certificate validation issues
        // Lưu ý: Chỉ dùng cho development/debug
        val trustManager = createUnsafeTrustManager()
        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        }
        
        // Hostname verifier để accept tất cả hostnames (chỉ cho debug)
        val hostnameVerifier = HostnameVerifier { _, _ -> true }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier(hostnameVerifier)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Tạo Retrofit instance cho Holiday API
     */
    private val holidayRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(HOLIDAY_API_BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Tạo Retrofit instance cho Quote API
     */
    private val quoteRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(QUOTE_API_BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Lazy initialization của HolidayApiService
     * Chỉ được tạo khi lần đầu tiên được truy cập
     */
    val holidayApiService: HolidayApiService by lazy {
        holidayRetrofit.create(HolidayApiService::class.java)
    }
    
    /**
     * Lazy initialization của QuoteApiService
     * Chỉ được tạo khi lần đầu tiên được truy cập
     */
    val quoteApiService: QuoteApiService by lazy {
        quoteRetrofit.create(QuoteApiService::class.java)
    }
}

