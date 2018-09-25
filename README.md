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
              implementation 'com.github.Awais9:VolleySingleCall:v1.0'
      }

# How to use?
Please check the example in the project which have following funtions.

      isFinishActivity()
      isShouldLogout()
      isShowLoadingDialog()
      setLogoutCount()
      isShowDialog()
      VolleyQueue.setHeaders()

Check the example to understand thier functionality.

