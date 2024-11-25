package net.adhikary.mrtbuddy.nfc

import android.nfc.tech.NfcF
import android.util.Log
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.parser.TransactionParser
import java.io.IOException

class NfcReader {
    private val commandGenerator = NfcCommandGenerator()

    fun readTransactionHistory(nfcF: NfcF): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val idm = nfcF.tag.id

        try {
            val command = commandGenerator.generateReadCommand(idm)
            val response1 = nfcF.transceive(command)
            transactions.addAll(TransactionParser.parseTransactionResponse(response1))

            // Second read command for blocks 10-19
            val command2 = commandGenerator.generateReadCommand(idm, startBlockNumber = 10)
            val response2 = nfcF.transceive(command2)
            transactions.addAll(TransactionParser.parseTransactionResponse(response2))
        } catch (e: IOException) {
            Log.e("NFC", "Error communicating with card", e)
        }

        return transactions
    }
}
