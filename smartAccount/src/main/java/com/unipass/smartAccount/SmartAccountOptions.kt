package com.unipass.smartAccount

import uniffi.shared.RoleWeight

class SmartAccountOptions(
    val masterKeySigner: Signer?,
    val masterKeyRoleWeight: RoleWeight?,
    val appId: String,
    val unipassServerUrl: String,
    val chainOptions: Array<ChainOptions>
)