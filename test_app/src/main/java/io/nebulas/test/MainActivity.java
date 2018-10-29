package io.nebulas.test;

import android.accounts.Account;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.math.BigDecimal;

import io.nebulas.walletcore.NebAccount;
import io.nebulas.walletcore.transaction.NebBinaryData;
import io.nebulas.walletcore.transaction.NebCallData;
import io.nebulas.walletcore.transaction.NebTransaction;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void AddressKeystoreTest() {
        NebAccount account = new NebAccount("5876489b63d456e3c82b043d235b46f76d4503b613230ed47d10bc4921e22f6a");
        Log.d("ACCOUNT", "address: " + account.getAddress());
        Log.d("ACCOUNT", "keystore: " + account.createNewKeystore("abc123"));

//        account = new NebAccount("{\"address\":\"n1JvS1LDTJRxSdq4F5cDd1x78ihHTTRyWif\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"4cec1d9cd72faff4c605a3f96aa91cc17d63b8fe072b7bc27f58a0b54180becf\",\"cipherparams\":{\"iv\":\"b25314e2ed860c5d842e903049f26312\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":1,\"r\":8,\"salt\":\"147248403048d5e59db5ed0dd0f3512a2a0cddc8bacd81b5c63a62fe3242cb32\"},\"mac\":\"e825044154cf0fbf2f3e84f0fe81bfae0eb7b9437d066bbb145260fb26ddb725\",\"machash\":\"sha3256\"},\"id\":\"a1c01502-9c7e-4a4a-aa45-0b66c85cd93d\",\"version\":4}", "111111");
        account = new NebAccount("{\"address\":\"n1JvS1LDTJRxSdq4F5cDd1x78ihHTTRyWif\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"573c0e3e8107e480c00458fd4b2d0a53\"},\"ciphertext\":\"512fd41ac302f191b107aee97e58128c6d3d1b87577d4e512e098ab7c6abf22aeb06b818fb558f7519c384458c41ca04\",\"kdf\":\"script\",\"kdfparams\":{\"c\":0,\"dklen\":32,\"n\":4096,\"p\":1,\"r\":8,\"salt\":\"e475313ec141a8e30173ecf855f88e84c7da3547156f25ddbe23e8105673343e\"},\"mac\":\"0835e1b07bb3871c0eb7b3838679bbf40e7cc103ac111648bf6265012735b319\",\"machash\":\"sha3256\"},\"id\":\"b2f0cc47-3f0d-4d74-8ec5-78ea8a608de3\",\"version\":4}", "abc123");
        Log.d("ACCOUNT", "address1: " + account.getAddress());
    }

    private void NormalTransactionTest() {
        NebAccount account = new NebAccount("5876489b63d456e3c82b043d235b46f76d4503b613230ed47d10bc4921e22f6a");

        NebTransaction tx = new NebTransaction();
        tx.chainID = 1;
        tx.from = account.getAddress();
        tx.to = account.getAddress();
        tx.value = new BigDecimal(0);
        tx.data = null; // new NebBinaryData("test");
        tx.nonce = 1;
        tx.gasLimit = new BigDecimal(2000000);
        tx.gasPrice = new BigDecimal(1000000);

        String rawTransaction = account.signTransaction(tx);

        // 可以将 rawTransaction 与 WebWallet 对比 以下为WebWallet结果：
        // CiA+fHNvmgi0rUcHlxRYn1T4fdG5QdZH2jLBmR+0IeJq0xIaGVcwVjTOPzQZ0fQrcMA3FHFbfv52gIw2A4QaGhlXMFY0zj80GdH0K3DANxRxW37+doCMNgOEIhAAAAAAAAAAAAAAAAAAAAAAKAEw/b3a3gU6CAoGYmluYXJ5QAFKEAAAAAAAAAAAAAAAAAAPQkBSEAAAAAAAAAAAAAAAAAADDUBYAWJBbhAgKt5OqNtxw6chgGqrnkYnJnsCQj8lKSxd9XZG3tJ0oiGETnawshDJI+75bC0U6IMdZcZcmdUinGfdxK4QwAE=
    }

    private void NRC20TransactionTest() {
        NebAccount account = new NebAccount("5876489b63d456e3c82b043d235b46f76d4503b613230ed47d10bc4921e22f6a");

        NebCallData data = new NebCallData();
        data.Function = "transfer";
        data.Args = "[\"" + account.getAddress() + "\", \"0\"]";

        NebTransaction tx = new NebTransaction();
        tx.chainID = 1;
        tx.from = account.getAddress();
        tx.to = "n1zUNqeBPvsyrw5zxp9mKcDdLTjuaEL7s39"; // ATP合约地址
        tx.value = new BigDecimal(0);
        tx.data = data;
        tx.nonce = 1;
        tx.gasLimit = new BigDecimal(2000000);
        tx.gasPrice = new BigDecimal(1000000);

        String rawTransaction = account.signTransaction(tx);

        // 可以将 rawTransaction 与 WebWallet 对比 以下为WebWallet结果：
        // CiDZZcleg55QSbmXDMdPORiFp7tYyFyXDyBNn3lDVBpe9xIaGVcwVjTOPzQZ0fQrcMA3FHFbfv52gIw2A4QaGhlY4i3HZHgLxsQzGRbnaJQcDfghMxXwzEoEIhAAAAAAAAAAAAAAAAAAAAAAKAEw67/a3gU6WQoEY2FsbBJReyJGdW5jdGlvbiI6InRyYW5zZmVyIiwiQXJncyI6IltcIm4xSnZTMUxEVEpSeFNkcTRGNWNEZDF4NzhpaEhUVFJ5V2lmXCIsIFwiMFwiXSJ9QAFKEAAAAAAAAAAAAAAAAAAPQkBSEAAAAAAAAAAAAAAAAAADDUBYAWJBTftyHqbpCWOkyiEsHhmnwY0CwqfKTkp8rKBbLAqEIncjelYmk0Xo0e49lTYuNiVo9q4qw0ZoRHDnNCnK6ndj0QE=
    }
}
