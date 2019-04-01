# VolleySingleCall
A custom volley library to handle all the network calls in single request except ftp.

# Add dependency?

Add the following in your main project gradle file
      
      allprojects {
          repositories {
            ...
            maven { url 'https://jitpack.io' }
          }
        }
        
And add the following in app gradle file

     dependencies {
             implementation 'com.github.Awais9:VolleySingleCall:v1.5.2'
      }

# How to use?
Please check the example in the project which have following funtions.

      isFinishActivity()
      isShouldLogout()
      setLogoutCount()
      isShowDialog()
      VolleyQueue.setHeaders()
      
Just call the service from network controller as below:

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
    
If you want to handle logout functionality then add eventbus in your app gradle as dependency and add the following method in your calling activity 

    @Subscribe
    fun logout(model: LogoutModel) {
        /*Clear all data in session*/
        val pm = packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

Check the example to understand all the functionality.

