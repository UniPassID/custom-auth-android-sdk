package com.unipass.smartAccount

import kotlin.jvm.JvmOverloads
import kotlinx.coroutines.coroutineScope
import org.web3j.utils.Numeric
import uniffi.shared.SendingTransactionOptions
import uniffi.shared.SimulateTransactionOptions
import uniffi.shared.SmartAccount
import uniffi.shared.SmartAccountBuilder
import uniffi.shared.Transaction
import uniffi.shared.SimulateResult
import uniffi.shared.TypedData

class SmartAccount(options: SmartAccountOptions) {
    var builder: SmartAccountBuilder?;
    var inner: SmartAccount? = null;

    init {
        builder = SmartAccountBuilder();
        if (options.masterKeySigner != null) {
            val masterKeySigner = WrapSigner(options.masterKeySigner);
            builder!!.withMasterKeySigner(masterKeySigner, null);
        }
        builder = builder!!.withAppId(options.appId).withUnipassServerUrl(options.unipassServerUrl);
        options.chainOptions.iterator().forEach { chainOptions ->
            builder!!.addChainOption(
                chainOptions.chainId.iD.toULong(),
                chainOptions.rpcUrl,
                chainOptions.relayerUrl
            )
        };
    }

    suspend fun init(options: SmartAccountInitOptions) {
        builder!!.withActiveChain(options.chainId.iD.toULong());
        return coroutineScope {
            inner = builder!!.build();
            builder!!.destroy();
            builder = null;
        }
    }

    suspend fun init(options: SmartAccountInitByKeysOptions) {
        builder =
            builder!!.withActiveChain(options.chainId.iD.toULong()).withKeys(options.keys.asList());
        return coroutineScope {
            inner = builder!!.build();
            builder!!.destroy();
            builder = null;
        }
    }

    /*********************** Account Status Functions  */
    fun address(): String {
        this.requireInit()
        return Numeric.toHexString(inner!!.address().toUByteArray().toByteArray())
    }

    //        throw new Exception("not implemented");
    suspend fun isDeployed(): Boolean {
        this.requireInit()
        return this.inner!!.isDeployed();
    }

    private fun requireInit() {
        if (inner == null) {
            throw SmartAccountNotInitException();
        }
    }

    fun chainId(): ChainID {
        requireInit();
        return ChainID.from(this.inner!!.chain().toInt());
    }

    suspend fun nonce(): ULong {
        requireInit()
        return this.inner!!.nonce()
    }

    fun switchChain(chainID: ChainID) {
        requireInit()
        this.inner!!.switchChain(chainID.iD.toULong())
    }

    /*********************** Message Sign Functions  */
    suspend fun signMessage(message: String): String? {
        return Numeric.toHexString(
            inner?.signMessage(message.toByteArray().toUByteArray().toList())?.toUByteArray()
                ?.toByteArray()
        );
    }

    suspend fun signMessage(messageBytes: ByteArray?): String? {
        return if (messageBytes == null) {
            null
        } else {
            Numeric.toHexString(
                inner?.signMessage(messageBytes.toUByteArray().toList())?.toUByteArray()
                    ?.toByteArray()
            )
        }
    }

    suspend fun signTypedData(typedData: TypedData): String {
        this.requireInit()
        return Numeric.toHexString(
            this.inner!!.signTypedData(typedData).toUByteArray().toByteArray()
        )
    }

    /*********************** Transaction Functions  */
    @JvmOverloads
    suspend fun simulateTransaction(
        tx: Transaction,
        options: SimulateTransactionOptions? = null
    ): SimulateResult? {
        return this.simulateTransactionBatch(arrayOf(tx), options)
    }

    @JvmOverloads
    suspend fun simulateTransactionBatch(
        txs: Array<Transaction>,
        options: SimulateTransactionOptions? = null
    ): SimulateResult? {
        return inner?.simulateTransactions(txs.asList(), options)
    }

    @JvmOverloads
    suspend fun sendTransaction(
        tx: Transaction,
        options: SendingTransactionOptions? = null
    ): String? {
        return this.sendTransactionBatch(arrayOf(tx), options)
    }

    @JvmOverloads
    suspend fun sendTransactionBatch(
        txs: Array<Transaction>,
        options: SendingTransactionOptions? = null
    ): String? {
        return inner?.sendTransactions(txs.asList(), options)
    }

    @JvmOverloads
    fun signTransaction(
        txs: Array<Transaction?>?,
        options: SendingTransactionOptions? = null
    ): Transaction? {
        return null
    }

    // TODO:
    fun sendSignedTransaction(): String? {
        return null
    }

    suspend fun waitTransactionReceiptByHash(
        transactionHash: String,
        confirmations: Int,
        chainId: ChainID?,
        timeOut: Int
    ): uniffi.shared.TransactionReceipt? {
        return inner?.waitForTransaction(transactionHash);
    }
}