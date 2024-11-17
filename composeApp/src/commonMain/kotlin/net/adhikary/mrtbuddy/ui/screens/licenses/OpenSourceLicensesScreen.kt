package net.adhikary.mrtbuddy.ui.screens.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import mrtbuddy.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalResourceApi::class)
@Composable
fun OpenSourceLicensesScreen(
    onBack: () -> Unit = {},
    paddingValues: PaddingValues
) {
    Column(Modifier.fillMaxSize().padding(paddingValues)) {
        TopAppBar(
            title = { Text("Open Source Licenses") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        var content by remember { mutableStateOf("") }


        val mdColors = DefaultMarkdownColors(
            text = colors.onSurface,
            codeText = colors.onSurface,
            inlineCodeText = colors.onSurface,
            linkText = colors.primary,
            codeBackground = colors.surface.copy(alpha = 0.1f),
            inlineCodeBackground = colors.surface.copy(alpha = 0.1f),
            dividerColor = colors.onSurface.copy(alpha = 0.12f)
        )
        val mdTypography = DefaultMarkdownTypography(
            text = MaterialTheme.typography.body2,
            code = MaterialTheme.typography.body2.copy(fontFamily = MaterialTheme.typography.body2.fontFamily),
            inlineCode = MaterialTheme.typography.body2.copy(fontFamily = MaterialTheme.typography.body2.fontFamily),
            h1 = MaterialTheme.typography.h6,
            h2 = MaterialTheme.typography.subtitle1,
            h3 = MaterialTheme.typography.subtitle2,
            h4 = MaterialTheme.typography.subtitle2,
            h5 = MaterialTheme.typography.subtitle2,
            h6 = MaterialTheme.typography.subtitle2,
            quote = MaterialTheme.typography.body2.copy(fontStyle = FontStyle.Italic),
            paragraph = MaterialTheme.typography.body2,
            ordered = MaterialTheme.typography.body2,
            bullet = MaterialTheme.typography.body2,
            list = MaterialTheme.typography.body2,
            link = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.primary)
        )

        LaunchedEffect(Unit) {
            content = Res.readBytes("files/open-source-licenses.md").decodeToString()
        }

        Markdown(
            content = content,
            modifier = Modifier.verticalScroll(rememberScrollState()),
            colors = mdColors,
            typography = mdTypography
        )
    }
}
