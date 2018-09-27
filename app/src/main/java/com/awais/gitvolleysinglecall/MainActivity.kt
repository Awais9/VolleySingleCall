package com.awais.gitvolleysinglecall

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.awais.volleysinglecall.LogoutModel
import com.awais.volleysinglecall.NetworkConst
import com.awais.volleysinglecall.NetworkController
import com.company.volleysinglecall.network.VolleyResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val testAPI: String = "Test"
    val baseURL = "your base URL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        callService()
    }

    private fun callService() {
        var serviceName = baseURL + "login"
        val networkController = NetworkController(this)
        networkController.isShowLoadingDialog = true
        val hashMap = HashMap<String, String>()
        hashMap["email"] = "username"
        hashMap["password"] = "password"
        networkController.isFinishActivity = true
        networkController.callService(NetworkConst.POST, serviceName, hashMap, testAPI,
                JSONObject::class.java, object : VolleyResponse {
            override fun onFailure(message: String) {

            }

            override fun onSuccess(response: Any, tag: String) {
                Log.e("Test", tag)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun logout(model: LogoutModel) {
        /*Clear all data in session*/
        val pm = packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
