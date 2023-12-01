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
    var gstNo : String? = null,
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
        if (phoneNumber == "1234567890"){
            return false
        }
        val allSameDigits = phoneNumber.all { it == phoneNumber[0] }
        if (allSameDigits){
            return false
        }
        val regex = Regex("\\d{10}")
        return phoneNumber.matches(regex)
    }

    fun hasValidPinCode(): Boolean {
        val pin = pinCode.toString()
        if (pin == "123456"){
            return false
        }
        val allSameDigits = pin.all { it == pin[0] }
        if (allSameDigits){
            return false
        }
        val regex = Regex("\\d{6}")
        return pin.matches(regex)
    }

}
