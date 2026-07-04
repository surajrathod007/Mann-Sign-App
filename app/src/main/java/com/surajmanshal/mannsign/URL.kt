package com.surajmanshal.mannsign

import com.surajmanshal.mannsign.BuildConfig

object URL {
//    ZTVPS
//    const val BASE_URL = "http://192.168.43.112:8080"+"/"   // Local
//    const val BASE_URL = "http://192.168.87.169:8080"+"/"   // Local 11 pro 5G
   val BASE_URL : String = BuildConfig.BASE_URL  // Server
//    const val BASE_URL = "https://ab1a-42-106-13-222.in.ngrok.io"+"/"
   val IMAGE_PATH : String = BASE_URL+"images/"
   val CHAT_IMAGE_PATH : String = BASE_URL+"chat/images/"
}
