package com.unipass.smartAccount

import uniffi.shared.RoleWeight

data class SmartAccountOptions(
    val masterKeySigner: Signer? = null,
    val masterKeyRoleWeight: RoleWeight? = null,
    val appId: String,
    val unipassServerUrl: String,
    val chainOptions: Array<ChainOptions>
)