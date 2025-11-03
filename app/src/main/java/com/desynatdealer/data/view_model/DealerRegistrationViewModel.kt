package com.desynatdealer.data.view_model

import androidx.lifecycle.ViewModel
import com.desynatdealer.model.storeDocumentAndBankDetails.StoreDocumentAndBankDetailsReq

class DealerRegistrationViewModel : ViewModel() {

    var storeInformation: HashMap<String, String>? = null

    var documentAndBankDetails: StoreDocumentAndBankDetailsReq? = null

    var previewData: HashMap<String, String>? = null
}