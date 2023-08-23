package com.surajmanshal.mannsign.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.surajmanshal.mannsign.databinding.ActivityContactUsBinding
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.makeACall


class ContactUsActivity : AppCompatActivity() {
    lateinit var binding: ActivityContactUsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            tvPhoneNo.text = Constants.MANN_SIGN_PHONE_NUMBER
            tvWhatAppNo.text = Constants.MANN_SIGN_PHONE_NUMBER
            tvEmail.text = Constants.MANN_SIGN_MAIL
            tvAddress.text = Constants.MANN_SIGN_ADDRESS

            ivBack.setOnClickListener{
                onBackPressedDispatcher.onBackPressed()
            }

            callLayout.setOnClickListener {
                makeACall(Constants.MANN_SIGN_PHONE_NUMBER)
            }

            waLayout.setOnClickListener {
                val url = "https://api.whatsapp.com/send?phone="+Constants.MANN_SIGN_PHONE_NUMBER
                val intent = Intent(Intent.ACTION_VIEW);
                intent.data = Uri.parse(url)
                startActivity(intent)
            }

            mailLayout.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", Constants.MANN_SIGN_MAIL, null))
                )
            }

            locationLayout.setOnClickListener {
                openLink(
                    "https://www.google.com/maps/place/MANN+SIGN+%2FSafety+Poster%2FSafety+Sign%2F5S+Poster%2FFire+Poster%2FManufacturers+%26+Suppliers+in+Ahmedabad%2FGujarat%2FIndia/@22.9494242,72.464506,16z/data=!4m6!3m5!1s0x395e9085a51544f3:0xa67fdd9e4968f6ae!8m2!3d22.9492167!4d72.4651444!16s%2Fg%2F11gfnw6dyw?entry=ttu"
                )
            }
            ivWebsite.setOnClickListener {
                openLink(Constants.WEBSITE)
            }
            ivIG.setOnClickListener {
                val uri = Uri.parse("http://instagram.com/_u/${Constants.IG_PAGE}")
                val intent = Intent(Intent.ACTION_VIEW, uri)

                intent.setPackage("com.instagram.android")

                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/${Constants.IG_PAGE}")
                        )
                    )
                }
            }
            ivFB.setOnClickListener {
                openLink(
                    "https://www.facebook.com/profile.php?id=61550310882789&mibextid=ZbWKwL"
                )
                /*startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/${Constants.FB_PAGE}")
                    ) //catches and opens a url to the desired page
                )*/
                /*val x = try {
                    packageManager.getPackageInfo("com.facebook.katana", PackageManager.PackageInfoFlags.of(0L))

                    Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/254175194653125")); //Trys to make intent with FB's URI
                } catch (e : Exception) {
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/arkverse")); //catches and opens a url to the desired page
                }*/
            }
        }
    }

    fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}