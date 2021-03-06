package com.awais.gitvolleysinglecall

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.awais.volleysinglecall.LogoutModel
import com.awais.volleysinglecall.NetworkConst
import com.awais.volleysinglecall.NetworkController
import com.awais.volleysinglecall.VolleyQueue
import com.company.volleysinglecall.network.VolleyResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val TAG: String = "Test"
    val baseURL = "http://"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        callService()
    }

    private fun callService() {
        var serviceName = baseURL /*+ "register"*/
        val headers = HashMap<String, String>()
        val queue = VolleyQueue.getInstance(this)
        queue.headers = headers

        val networkController = NetworkController(this)
        val params = JSONObject()
        params.put("Email", "test11@gmail.com")
        params.put("Password", "123456789")
        params.put("MobileNumber", "+9230064000000")
        params.put("FirstName", "Test")
        params.put("LastName", "Test")

        networkController.isFinishActivity = true
        networkController.callService(NetworkConst.POST, serviceName, null, TAG,
                JSONObject::class.java, object : VolleyResponse {
            override fun noInternet() {
                Log.e("Log", "If internet is not connected")
            }

            override fun onSuccess(response: Any, tag: String) {
                Log.e("onSuccess", tag)
            }

            override fun onFailure(message: String) {
                Log.e("onFailure", message)
            }
        }, params, true)
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
