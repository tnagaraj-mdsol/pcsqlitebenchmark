package com.mdsol.sqlitebenchmark

import android.content.Context
import android.util.Base64
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtil {

    const val AES_ENCRYPTION_KEY = "mitosis_aes_encryption_key"
    const val AES_ENCRYPTION_IV = "mitosis_aes_encryption_iv"
    private const val AES_GCM_NO_PADDING_MODE = "AES/GCM/NoPadding"
    const val CRYPTO_METHOD_AES = "AES"
    private var context: Context? = null

    fun getAesEncryptionKeyAndIV(): Pair<ByteArray, ByteArray> {
        val preferences = context?.getSharedPreferences("encryption", Context.MODE_PRIVATE)!!
        val aesKeyBase64String = preferences.getString(AES_ENCRYPTION_KEY, "")
        val aesIVBase64String = preferences.getString(AES_ENCRYPTION_IV, "")
        val key = Base64.decode(aesKeyBase64String, Base64.NO_WRAP)
        val iv = Base64.decode(aesIVBase64String, Base64.NO_WRAP)

        return Pair(key, iv)
    }

    @Synchronized
    internal fun generateAesKeyAndIVForDataEncryption(context: Context) {
        this.context = context
        val preferences = context.getSharedPreferences("encryption", Context.MODE_PRIVATE)

        if (preferences.contains(AES_ENCRYPTION_KEY)) {
            return
        }

        val random = SecureRandom()

        val aesKey = ByteArray(32)
        val aesIV = ByteArray(16)

        random.nextBytes(aesKey)
        random.nextBytes(aesIV)

        val aesKeyBase64String = Base64.encodeToString(aesKey, Base64.NO_WRAP)
        val aesIVBase64String = Base64.encodeToString(aesIV, Base64.NO_WRAP)

        with(preferences.edit()) {
            putString(AES_ENCRYPTION_KEY, aesKeyBase64String)
            putString(AES_ENCRYPTION_IV, aesIVBase64String)
            apply()
        }
    }

    fun encryptWithAES(
        bytesToEncrypt: ByteArray,
        key: ByteArray,
        iv: ByteArray
    ): Pair<ByteArray, ByteArray> {
        // Use AES256 to encrypt the plaintext
        // https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html
        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING_MODE)
        val keySpec = SecretKeySpec(key, 0, 32, CRYPTO_METHOD_AES)
        val ivSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encrypted = cipher.doFinal(bytesToEncrypt)

        // Separate out the result and the tag
        val res = Arrays.copyOfRange(encrypted, 0, encrypted.size - (128 / 8))
        val tag = Arrays.copyOfRange(encrypted, encrypted.size - (128 / 8), encrypted.size)

        return Pair(res, tag)
    }

    /**
     * Decrypt cipher data using AES 256
     */
    fun decryptWithAES(cipherText: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(key, 0, 32, CRYPTO_METHOD_AES)
        val gcmSpec = GCMParameterSpec(128, iv)
        val aesCipher = Cipher.getInstance(AES_GCM_NO_PADDING_MODE)
        aesCipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)

        return aesCipher.doFinal(cipherText)
    }
}