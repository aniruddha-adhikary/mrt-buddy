package net.adhikary.mrtbuddy.nfc

import android.nfc.tech.NfcF
import android.util.Log
import net.adhikary.mrtbuddy.model.Transaction
import net.adhikary.mrtbuddy.nfc.parser.ByteParser
import net.adhikary.mrtbuddy.nfc.parser.TransactionParser
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import java.io.IOException

class NfcReader {
    private val byteParser = ByteParser()
    private val timestampService = TimestampService()
    private val stationService = StationService()
    private val transactionParser = TransactionParser(byteParser, timestampService, stationService)
    private val commandGenerator = NfcCommandGenerator()

    fun readTransactionHistory(nfcF: NfcF): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val idm = nfcF.tag.id

        try {
            val command = commandGenerator.generateReadCommand(idm)
            val response = nfcF.transceive(command)
            transactions.addAll(transactionParser.parseTransactionResponse(response))
        } catch (e: IOException) {
            Log.e("NFC", "Error communicating with card", e)
        }

        return transactions
    }
}