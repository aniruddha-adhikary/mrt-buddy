package net.adhikary.mrtbuddy.utils

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class FileSharer {
    fun share(content: String, filename: String, mimeType: String)
}
