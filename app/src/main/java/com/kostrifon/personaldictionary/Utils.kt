package com.kostrifon.personaldictionary

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.cio.writeChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.io.copyAndClose
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.CipherSuite.*
import okhttp3.ConnectionSpec
import okhttp3.TlsVersion
import java.io.File
import java.io.IOException
import java.net.URL
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.net.ssl.SSLContext


data class HttpClientException(val response: HttpResponse) : IOException("HTTP Error ${response.status}")

@KtorExperimentalAPI
suspend fun HttpClient.getAsFile(url: String, path: String): File {
    val file = File(path)
    GlobalScope.launch(Dispatchers.IO) { file.createNewFile() }
    val response = request<HttpResponse> {
        url(URL(url))
        method = HttpMethod.Get
    }
    if (!response.status.isSuccess()) {
        throw HttpClientException(response)
    }
    response.content.copyAndClose(file.writeChannel())
    return file
}

@KtorExperimentalAPI
fun download(link: String, path: String): File {

    val spec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
        .cipherSuites(TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256)
        .build()

    return runBlocking {
        HttpClient(OkHttp) {
            followRedirects = true
            engine {
                config {
                    connectionSpecs(Collections.singletonList(spec))
                }
            }
        }.getAsFile(link, path)
    }
}

@KtorExperimentalAPI
fun downloadCompat(context: Context?, link: String, path: String): File {
    try {
        // Google Play will install latest OpenSSL
        ProviderInstaller.installIfNeeded(context)
        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(null, null, null)
        sslContext.createSSLEngine()
    } catch (e: GooglePlayServicesRepairableException) {
        e.printStackTrace()
    } catch (e: GooglePlayServicesNotAvailableException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: KeyManagementException) {
        e.printStackTrace()
    }

    return download(link, path)
}

@SuppressLint("SetWorldReadable")
fun playSound(context: Context, path: String) {
    val file = File(path)
    file.setReadable(true, false)
    MediaPlayer().apply {
        setDataSource(context, Uri.parse(file.path))
        prepare()
        setOnPreparedListener { it.start() }
    }
}

fun hideSoftKeyboard(activity: Activity, view: View) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun String.getFileName() = this.substringAfterLast("/")

@Suppress("DEPRECATION")
fun isConnected(context: Context) =
    (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnectedOrConnecting == true
