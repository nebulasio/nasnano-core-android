package io.nebulas.test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.nebulas.walletcore.NebAccount
import io.nebulas.walletcore.exceptions.IllegalKeystoreException
import io.nebulas.walletcore.exceptions.IllegalPrivateKeyException
import io.nebulas.walletcore.exceptions.NebulasException
import io.nebulas.walletcore.exceptions.WrongPasswordException
import io.nebulas.walletcore.transaction.NebCallData
import io.nebulas.walletcore.transaction.NebTransaction
import kotlinx.android.synthetic.main.activity_main.*
import neoutils.Neoutils
import java.math.BigDecimal
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val tag = "WalletResult"
    private val chainIdMainNet = 1
    private val chainIdTestNet = 1001

    private val testPrivateKey = "99856337acfae923d0d49baad077a45bc110154cb3b9775f611f2d2b6c87d818"
    private val testKeystore = "{\"address\":\"n1NU4imP3aD6NcQVEBPpZ53WGHkAfGoCsh5\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"9eb86cec915f9c9df5a6d225cdf4485d\"},\"ciphertext\":\"dbc4ba7741e6b21279c26a6fbee9e86e1e4ccf673e041e48bbe2d40661f7464ff6d2bd43a16c341491bb94092fb2ba12\",\"kdf\":\"script\",\"kdfparams\":{\"c\":0,\"dklen\":32,\"n\":4096,\"p\":1,\"r\":8,\"salt\":\"2cf28fecd199600036156721d7bfcd8eb40fc9edc3d08ae44e173c055c6314c6\"},\"mac\":\"8acbf2d441bab3c9bbabfa32b2f1ebee4ab1b87ad6e6920d3f04938334d8e0a0\",\"machash\":\"sha3256\"},\"id\":\"a9d4874e-7024-4e8d-b487-f3edef7d9865\",\"version\":4}"
    private val testPassword = "abc123"

    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newCachedThreadPool()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_to_create_neo_wallet.setOnClickListener { createNeoWallet() }
        btn_to_create_nas_wallet.setOnClickListener { createNasWallet() }
        btn_to_import_nas_wallet_by_pk.setOnClickListener { importNasWalletByPrivateKey() }
        btn_to_import_nas_wallet_by_keystore.setOnClickListener { importNasWalletByKeystore() }
        btn_to_get_raw_transaction.setOnClickListener { doNormalNasTransaction() }
        btn_to_get_nrc20_raw_transaction.setOnClickListener { doNrc20Transaction() }
    }

    private fun updateResult(result: String) {
        mainHandler.post {
            Log.d(tag, result)
            tv_result.text = result
        }
    }

    private fun createNeoWallet() {
        executor.submit {
            val neoWallet = Neoutils.newWallet()
            updateResult("NEO Wallet Address: \n\n${neoWallet.address}")
        }
    }

    private fun createNasWallet() {
        executor.submit {
            val nasWallet = NebAccount()
            updateResult("NAS Wallet Address: \n\n${nasWallet.address}")
        }
    }

    private fun importNasWalletByPrivateKey() {
        executor.submit {
            try {
                val nasWallet = NebAccount(testPrivateKey)
                updateResult("Imported(PK) NAS Wallet - Address: \n\n${nasWallet.address}")
            } catch (e: NebulasException) {
                when(e){
                    is IllegalPrivateKeyException -> {
                        //通过私钥导入钱包（即：NebAccount(privateKey)构造方法），只会抛出IllegalPrivateKeyException
                        //do something when illegal private key
                    }
                    is IllegalKeystoreException -> {
                        //do something when illegal keystore
                    }
                    is WrongPasswordException -> {
                        //do something when password is wrong
                    }
                }
                updateResult("Import(PK) NAS Wallet - Error: \n\n${e.errorCode} - ${e.message}")
            }
        }
    }

    private fun importNasWalletByKeystore() {
        executor.submit {
            try {
                val nasWallet = NebAccount(testKeystore, testPassword)
                updateResult("Imported(Keystore) NAS Wallet - Address: \n\n${nasWallet.address}")
            } catch (e: NebulasException) {
                when(e){
                    is IllegalPrivateKeyException -> {
                        //do something when illegal private key
                    }
                    is IllegalKeystoreException -> {
                        //通过私钥导入钱包（即：NebAccount(keystore, password)构造方法），可能会抛出: IllegalKeystoreException、WrongPasswordException
                        //do something when illegal keystore
                    }
                    is WrongPasswordException -> {
                        //通过私钥导入钱包（即：NebAccount(keystore, password)构造方法），可能会抛出: IllegalKeystoreException、WrongPasswordException
                        //do something when password is wrong
                    }
                }
                updateResult("Import(Keystore) NAS Wallet - Error: \n\n${e.errorCode} - ${e.message}")
            }
        }
    }

    private fun doNormalNasTransaction() {
        executor.submit {
            var account: NebAccount? = null
            try {
                account = NebAccount(testPrivateKey)
            } catch (e: NebulasException) {
                e.printStackTrace()
                return@submit
            }

            val tx = NebTransaction()
            tx.chainID = chainIdTestNet
            tx.from = account.address
            tx.to = account.address
            tx.value = BigDecimal("0")
            tx.data = null//new NebBinaryData("test");
            tx.nonce = 1
            tx.gasLimit = BigDecimal("2000000")
            tx.gasPrice = BigDecimal("1000000")

            val rawTransaction = account.signTransaction(tx)
            updateResult("rawTransaction: \n\n$rawTransaction")

            //可在终端中用如下命令测试
            //curl -i -H 'Content-Type: application/json' -X POST http://13.57.96.40:8685/v1/user/rawtransaction -d '{"data":"日志中打印出来的rawTransaction"}'
        }
    }

    private fun doNrc20Transaction() {
        executor.submit {
            var account: NebAccount? = null
            try {
                account = NebAccount(testPrivateKey)
            } catch (e: NebulasException) {
                e.printStackTrace()
                return@submit
            }

            val data = NebCallData()
            data.Function = "transfer"
            data.Args = "[\"" + account.address + "\", \"0\"]"

            val tx = NebTransaction()
            tx.chainID = chainIdTestNet
            tx.from = account.address
            tx.to = "n1zUNqeBPvsyrw5zxp9mKcDdLTjuaEL7s39" // ATP合约地址
            tx.value = BigDecimal("0")
            tx.data = data
            tx.nonce = 1
            tx.gasLimit = BigDecimal("2000000")
            tx.gasPrice = BigDecimal("1000000")

            val rawTransaction = account.signTransaction(tx)
            updateResult("Nrc20RawTransaction: \n\n$rawTransaction")

            //可在终端中用如下命令测试
            //curl -i -H 'Content-Type: application/json' -X POST http://13.57.96.40:8685/v1/user/rawtransaction -d '{"data":"日志中打印出来的rawTransaction"}'
        }
    }
}
