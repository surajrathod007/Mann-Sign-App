package com.surajmanshal.mannsign.data.model.auth

data class User(
    var emailId : String ="",
    var password : String ="",
    var firstName : String? = null,
    val lastName : String? = null,
    var phoneNumber : String = "",
    var profileImage : String? = null,
    val address : String? = null,
    var pinCode : Int? = null,
    var token : String = "",
    var deviceId : String? = null,
    var profilePic : String? = null,
    var gstNo : String? = null
) : java.io.Serializable {
    fun hasSufficientProfileDetails(): Boolean {
        return emailId.isNotEmpty() &&
                !firstName.isNullOrBlank() &&
                !lastName.isNullOrBlank() &&
                phoneNumber.isNotBlank() &&
                !address.isNullOrBlank() &&
                pinCode != null
    }

    fun hasValidPhoneNumber(): Boolean {
        val regex = Regex("\\d{10}")
        return phoneNumber.matches(regex)
    }

    fun hasValidPinCode(): Boolean {
        val regex = Regex("\\d{6}")
        return pinCode.toString().matches(regex)
    }

}
