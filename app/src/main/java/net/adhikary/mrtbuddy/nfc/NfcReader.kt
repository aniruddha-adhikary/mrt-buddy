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

    fun readTransactionHistory(nfcF: NfcF): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val idm = nfcF.tag.id
        val serviceCode = 0x220F
        val serviceCodeList = byteArrayOf(
            (serviceCode and 0xFF).toByte(),
            ((serviceCode shr 8) and 0xFF).toByte()
        )

        val numberOfBlocksToRead = 10

        val blockListElements = ByteArray(numberOfBlocksToRead * 2)
        for (i in 0 until numberOfBlocksToRead) {
            blockListElements[i * 2] = 0x80.toByte()
            blockListElements[i * 2 + 1] = i.toByte()
        }

        val commandLength = 14 + blockListElements.size
        val command = ByteArray(commandLength)
        var idx = 0
        command[idx++] = commandLength.toByte()
        command[idx++] = 0x06.toByte()
        System.arraycopy(idm, 0, command, idx, idm.size)
        idx += idm.size
        command[idx++] = 0x01.toByte()
        command[idx++] = serviceCodeList[0]
        command[idx++] = serviceCodeList[1]
        command[idx++] = numberOfBlocksToRead.toByte()
        System.arraycopy(blockListElements, 0, command, idx, blockListElements.size)

        try {
            val response = nfcF.transceive(command)
            transactions.addAll(transactionParser.parseTransactionResponse(response))
        } catch (e: IOException) {
            Log.e("NFC", "Error communicating with card", e)
        }

        return transactions
    }
}