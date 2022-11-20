package com.surajmanshal.mannsign.data.model.ordering

data class TransactionItem(
    val transaction : Transaction,
    var visible : Boolean = false
)
