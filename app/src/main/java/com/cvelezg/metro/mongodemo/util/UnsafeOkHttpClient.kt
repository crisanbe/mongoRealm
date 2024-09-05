//package com.cvelezg.metro.mongodemo.util
//
//import okhttp3.OkHttpClient
//import java.security.SecureRandom
//import java.security.cert.X509Certificate
//import javax.net.ssl.HostnameVerifier
//import javax.net.ssl.SSLContext
//import javax.net.ssl.SSLSession
//import javax.net.ssl.SSLSocketFactory
//import javax.net.ssl.TrustManager
//import javax.net.ssl.X509TrustManager
//
//
//object UnsafeOkHttpClient {
//    val unsafeOkHttpClient: OkHttpClient
//        get() {
//            try {
//                // Instala un TrustManager que acepta todos los certificados
//                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
//                    override fun checkClientTrusted(
//                        chain: Array<X509Certificate>,
//                        authType: String
//                    ) {
//                    }
//
//                    override fun checkServerTrusted(
//                        chain: Array<X509Certificate>,
//                        authType: String
//                    ) {
//                    }
//
//                    override fun getAcceptedIssuers(): Array<X509Certificate> {
//                        return arrayOf()
//                    }
//                }
//                )
//
//                // Crear un SSLContext que usa el TrustManager
//                val sslContext = SSLContext.getInstance("SSL")
//                sslContext.init(null, trustAllCerts, SecureRandom())
//
//                // Crear un socket factory con nuestro TrustManager
//                val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
//
//                val builder: Builder = Builder()
//                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
//                builder.hostnameVerifier(HostnameVerifier { hostname: String?, session: SSLSession? -> true })
//
//                return builder.build()
//            } catch (e: Exception) {
//                throw RuntimeException(e)
//            }
//        }
//}
