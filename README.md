# VolleySingleCall
A custom volley library to handle all the network calls in single request except ftp.

# Add dependency?

Add the following in your main project gradle file

      dependencies {
          classpath 'com.github.dcendents:android-maven-gradle-plugin:1.4.1'
      }
      
      allprojects {
          repositories {
            ...
            maven { url 'https://jitpack.io' }
          }
        }
        
And add the following in app gradle file

     dependencies {
              implementation 'com.github.Awais9:VolleySingleCall:v1.2'
      }

# How to use?
Please check the example in the project which have following funtions.

      isFinishActivity()
      isShouldLogout()
      isShowLoadingDialog()
      setLogoutCount()
      isShowDialog()
      getLoadingDialog()
      VolleyQueue.setHeaders()
      
Just call the service from network controller as below:

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

