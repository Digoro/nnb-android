package com.nnb.nnb_android

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URISyntaxException
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    private var filePathCallbackNormal: ValueCallback<Uri>? = null
    private var filePathCallbackLollipop: ValueCallback<Array<Uri>>? = null
    private val fileChooserNormalReqCode = 1
    private val fileChooserLollipopReqCode = 2

    private var apiSendCheck = false
    private var apiFcmCheck = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fileChooserNormalReqCode) {
                filePathCallbackNormal?.let {
                    val result =
                        if (data == null || resultCode != Activity.RESULT_OK) null else data.data
                    it.onReceiveValue(result)
                    filePathCallbackNormal = null
                }
            } else if (requestCode == fileChooserLollipopReqCode) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setFilePathCallbackValue(
                        WebChromeClient.FileChooserParams.parseResult(
                            resultCode,
                            data
                        )
                    )
                }
            }
        } else {
            setFilePathCallbackValue(null)
        }
    }

    private fun setFilePathCallbackValue(value: Any?) {
        filePathCallbackLollipop?.let {
            it.onReceiveValue(value as Array<Uri>?)
            filePathCallbackLollipop = null
        }
    }

    private fun setToken(token: String?) {
        val pref: SharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("token", token)
        editor.commit()
    }

    private fun getToken(): String? {
        val pref: SharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        return pref.getString("token", "")
    }

    private fun setLoginToken(token: String?) {
        val pref: SharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("login_token", token)
        editor.commit()
    }

    private fun getLoginToken(): String? {
        val pref: SharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        return pref.getString("login_token", "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var token = FirebaseInstanceId.getInstance().token

        if (token == null) {
            token = ""
        }
        Log.d("TOKEN VALUE: ", token)
        setToken(token)
        Log.d("Get Token VALUE: ", getToken().toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // ????????? ?????? ???????????? ????????? ?????? ??? ????????? ??????
            val channelId = "MY_channel" // ????????? ?????? ?????? id ??????
            val channelName = "????????????" // ?????? ?????? ??????
            val descriptionText = "?????????" // ?????? ????????? ??????
            val importance = NotificationManager.IMPORTANCE_DEFAULT // ?????? ???????????? ??????
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            // ?????? ?????? ????????? ???????????? ??????
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val cm: CookieManager = CookieManager.getInstance()
        cm.setAcceptCookie(true);
        val userAgent = webView.settings.userAgentString

        webView.settings.run {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = false
            javaScriptCanOpenWindowsAutomatically = true
            allowFileAccess = true
            pluginState = WebSettings.PluginState.ON
            cacheMode = WebSettings.LOAD_NO_CACHE
            userAgentString = "$userAgent NNB_ANDROID_AGENT"
            setSupportMultipleWindows(true)
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                dialog: Boolean,
                userGesture: Boolean,
                resultMsg: android.os.Message
            ): Boolean {
                val newWebView = WebView(view.context)
                val transport = resultMsg.obj as WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams
            ): Boolean {
                setFilePathCallbackValue(null)
                filePathCallbackLollipop = filePathCallback
                Intent(Intent.ACTION_GET_CONTENT).run {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                    startActivityForResult(
                        Intent.createChooser(this, "File Chooser"),
                        fileChooserLollipopReqCode
                    )
                }
                return true
            }
        }
        webView.webViewClient = object : WebViewClient() {
            // ????????????????????? ????????? ?????? ????????? ???????????? ???????????? ?????? ??????
            // https://developers.kakao.com/docs/latest/ko/getting-started/sdk-js#run-kakaotalk
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("intent:") || url.startsWith("nidlogin://")) {
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        val uri = Uri.parse(intent.dataString)
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                        return true
                    } catch (ex: URISyntaxException) {
                        return false
                    } catch (e: ActivityNotFoundException) {
                        if (intent == null) return false
                        if(url.contains("intent:kakaolink://send")){
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.kakao.talk")
                                )
                            )
                            return true
                        } else {
                            val packageName = intent.getPackage()
                            if (packageName != null) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=$packageName")
                                    )
                                )
                                return true
                            }
                            return false
                        }
                    }
                } else if (url.startsWith("nonunbub://")) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.addCategory(Intent.CATEGORY_BROWSABLE)
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.data = Uri.parse("nonunbub://")
                    startActivity(intent)
                } else if (url.contains("notion.so/nonunbub") || url.contains("nonunbub-host.oopy.io")) {
                    webView.loadUrl(url)
                } else if (url.startsWith("tel:")) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent)
                    return true
                } else if (url.startsWith("mailto:")) {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                    startActivity(intent)
                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                val webview1 = WebView(baseContext)
                val cookies = CookieManager.getInstance().getCookie(view.url)
                var loginCheck = false

                if (cookies != null) {
                    val temp = cookies.split(";").toTypedArray()
                    for (it in temp) {
                        if (it.contains("access_token")) {
                            loginCheck = true
                            val value = it.split("=")
                            setLoginToken(value[1]);
                            Log.d("access_token", value[1])
                        }
                    }
                }

                if (FirebaseInstanceIDService.frm_delete) {
                    FirebaseInstanceIDService.frm_delete = false
                    val url = "https://nonunbub.com/api/fcm/token/delete"
                    val postData =
                        "token=" + URLEncoder.encode(FirebaseInstanceIDService.beforeToken, "UTF-8")
                            .toString()
                    webview1.postUrl(url, postData.toByteArray())
                }

                if (!loginCheck) {
                    setLoginToken("")
                    apiSendCheck = false
                }

                // FCM ?????? ?????? ??????
                // ?????? ?????? ?????? ??? ??????
                if (!apiFcmCheck && getToken() != "") {
                    apiFcmCheck = true
                    val url = "https://nonunbub.com/api/fcm/token"
                    val postData = "token=" + URLEncoder.encode(getToken(), "UTF-8")
                        .toString()
                    webview1.postUrl(url, postData.toByteArray())
                }

                // FCM ?????? ??? Login ?????? ??????
                // ?????? ?????? ?????? ??? ??????
                try {
                    if (!apiSendCheck && getLoginToken() != "" && getToken() != "") {
                        apiSendCheck = true
                        //setContentView(webview1)
                        val url = "https://nonunbub.com/api/fcm/token"
                        val postData = "token=" + URLEncoder.encode(getToken(), "UTF-8")
                            .toString() + "&accessToken=" + URLEncoder.encode(
                            getLoginToken(),
                            "UTF-8"
                        )
                        webview1.postUrl(url, postData.toByteArray())
                    }
                } catch (ex: NumberFormatException) {

                }
            }
        }
        // http??? ????????? ?????? ????????? ??? ????????? ???
        // https://mixup.tistory.com/29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        var intent = getIntent()
        var bundle = intent.getExtras()

        if (bundle != null) {
            if (!bundle.getString("url").isNullOrEmpty()) {
                webView.loadUrl(bundle.getString("url").toString())
                bundle.putString("url", "")
            } else {
                webView.loadUrl("https://nonunbub.com")
            }
        } else {
            webView.loadUrl("https://nonunbub.com")
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }
}

