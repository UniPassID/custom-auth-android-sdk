package com.unipass.smartAccount

class SmartAccountOptions(
    val masterKeySigner: Signer,
    val appId: String,
    val unipassServerUrl: String,
    val chainOptions: Array<ChainOptions>
)