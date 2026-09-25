package net.adhikary.mrtbuddy.ui.screens.more

import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.payments
import mrtbuddy.composeapp.generated.resources.recharge
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MoreScreenResourcesTest {
    @Test
    fun testRechargeStringAndDrawableResourcesExist() {
        assertNotNull(Res.string.recharge)
        assertEquals("recharge", Res.string.recharge.key)
        assertNotNull(Res.drawable.payments)
    }

    @Test
    fun testEnglishStringsXmlContainsRecharge() {
        val stringsFile = findFile("composeApp/src/commonMain/composeResources/values/strings.xml")
        val value = parseStringResource(stringsFile, "recharge")
        assertEquals("Recharge", value)
    }

    @Test
    fun testBengaliStringsXmlContainsRecharge() {
        val stringsFile = findFile("composeApp/src/commonMain/composeResources/values-bn/strings.xml")
        val value = parseStringResource(stringsFile, "recharge")
        assertEquals("রিচার্জ", value)
    }

    @Test
    fun testEnglishStringsXmlContainsRedirectDisclaimer() {
        val stringsFile = findFile("composeApp/src/commonMain/composeResources/values/strings.xml")
        val title = parseStringResource(stringsFile, "externalRedirectTitle")
        val message = parseStringResource(stringsFile, "externalRedirectMessage")
        val continueBtn = parseStringResource(stringsFile, "continueButton")
        assertEquals("Leaving MRT Buddy", title)
        assertNotNull(message)
        assertTrue(
            message!!.contains("third-party"),
            "Redirect message must mention third-party",
        )
        assertEquals("Continue", continueBtn)
    }

    @Test
    fun testBengaliStringsXmlContainsRedirectDisclaimer() {
        val stringsFile = findFile("composeApp/src/commonMain/composeResources/values-bn/strings.xml")
        val title = parseStringResource(stringsFile, "externalRedirectTitle")
        val message = parseStringResource(stringsFile, "externalRedirectMessage")
        val continueBtn = parseStringResource(stringsFile, "continueButton")
        assertNotNull(title)
        assertNotNull(message)
        assertNotNull(continueBtn)
    }

    @Test
    fun testMoreScreenContainsRechargeButtonBeforeStationMap() {
        val moreScreenFile = findFile("composeApp/src/commonMain/kotlin/net/adhikary/mrtbuddy/ui/screens/more/MoreScreen.kt")
        val content = moreScreenFile.readText()

        val othersIndex = content.indexOf("SectionHeader(text = stringResource(Res.string.others))")
        val stationMapIndex = content.indexOf("text = stringResource(Res.string.stationMap)")

        assertTrue(othersIndex != -1, "MoreScreen.kt should contain SectionHeader for others")
        assertTrue(stationMapIndex != -1, "MoreScreen.kt should contain stationMap button")
        assertTrue(othersIndex < stationMapIndex, "Others section header must be before stationMap")

        val rechargeTextIndex = content.indexOf("text = stringResource(Res.string.recharge)")
        assertTrue(rechargeTextIndex != -1, "MoreScreen.kt should contain recharge button")
        assertTrue(
            othersIndex < rechargeTextIndex && rechargeTextIndex < stationMapIndex,
            "Recharge button must be between Others section header and stationMap",
        )

        val betweenOthersAndRecharge = content.substring(othersIndex, rechargeTextIndex)
        assertEquals(
            1,
            betweenOthersAndRecharge.split("RoundedButton(").size - 1,
            "Recharge button must be the first RoundedButton under Others section",
        )

        val betweenRechargeAndStationMap = content.substring(rechargeTextIndex, stationMapIndex)
        assertEquals(
            1,
            betweenRechargeAndStationMap.split("RoundedButton(").size - 1,
            "Station map must immediately follow Recharge with no intervening buttons",
        )

        val aboutHeaderIndex = content.indexOf("SectionHeader(text = stringResource(Res.string.aboutHeader))")
        assertTrue(aboutHeaderIndex != -1, "MoreScreen.kt should contain SectionHeader for aboutHeader")
        assertTrue(stationMapIndex < aboutHeaderIndex, "StationMap must be before aboutHeader")

        val othersSectionFullBlock = content.substring(othersIndex, aboutHeaderIndex)
        assertEquals(
            2,
            othersSectionFullBlock.split("RoundedButton(").size - 1,
            "Others section must contain exactly 2 buttons: Recharge and Station Map",
        )
    }

    @Test
    fun testMoreScreenRechargeButtonAndDialogConfigured() {
        val moreScreenFile = findFile("composeApp/src/commonMain/kotlin/net/adhikary/mrtbuddy/ui/screens/more/MoreScreen.kt")
        val content = moreScreenFile.readText()

        val othersIndex = content.indexOf("SectionHeader(text = stringResource(Res.string.others))")
        val stationMapIndex = content.indexOf("text = stringResource(Res.string.stationMap)")
        val rechargeSectionBlock = content.substring(othersIndex, stationMapIndex)

        assertTrue(
            rechargeSectionBlock.contains("RoundedButton("),
            "Recharge button must be a RoundedButton",
        )
        assertTrue(
            rechargeSectionBlock.contains("text = stringResource(Res.string.recharge)"),
            "Recharge button must use Res.string.recharge",
        )
        assertTrue(
            rechargeSectionBlock.contains("painter = painterResource(Res.drawable.payments)"),
            "Recharge button must use Res.drawable.payments",
        )
        assertTrue(
            rechargeSectionBlock.contains("showRedirectDialog = true"),
            "Recharge button onClick must trigger the redirect disclaimer dialog",
        )
        assertTrue(
            content.contains("uriHandler.openUri(\"https://rapidpass.com.bd/en/login\")"),
            "MoreScreen must contain rapidpass login URL in the redirect dialog",
        )
        assertTrue(
            content.contains("Res.string.externalRedirectTitle"),
            "MoreScreen must contain the external redirect disclaimer title",
        )
        assertTrue(
            content.contains("Res.string.externalRedirectMessage"),
            "MoreScreen must contain the external redirect disclaimer message",
        )
    }

    @Test
    fun testPaymentsDrawableVectorXmlIsValid() {
        val paymentsFile = findFile("composeApp/src/commonMain/composeResources/drawable/payments.xml")
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(paymentsFile)
        val root = doc.documentElement
        assertEquals("vector", root.tagName)
        assertEquals("24dp", root.getAttribute("android:width"))
        assertEquals("24dp", root.getAttribute("android:height"))

        val pathNodes = doc.getElementsByTagName("path")
        assertTrue(pathNodes.length > 0, "payments.xml must contain at least one path element")
        val pathElement = pathNodes.item(0) as Element
        assertTrue(
            pathElement.getAttribute("android:pathData").isNotBlank(),
            "payments.xml path element must have non-empty android:pathData",
        )
    }

    private fun findFile(relativePath: String): File {
        var file = File(relativePath)
        if (file.exists()) return file
        file = File("../$relativePath")
        if (file.exists()) return file
        val root = File(System.getProperty("user.dir") ?: ".")
        val candidate = File(root, relativePath)
        if (candidate.exists()) return candidate
        val parentCandidate = File(root.parentFile, relativePath)
        if (parentCandidate.exists()) return parentCandidate
        throw IllegalArgumentException("Could not find file: $relativePath from ${root.absolutePath}")
    }

    private fun parseStringResource(
        file: File,
        name: String,
    ): String? {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val stringNodes = doc.getElementsByTagName("string")
        for (i in 0 until stringNodes.length) {
            val node = stringNodes.item(i) as Element
            if (node.getAttribute("name") == name) {
                return node.textContent
            }
        }
        return null
    }
}
