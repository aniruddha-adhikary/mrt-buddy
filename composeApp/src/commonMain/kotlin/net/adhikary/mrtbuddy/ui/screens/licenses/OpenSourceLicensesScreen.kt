package net.adhikary.mrtbuddy.ui.screens.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import mrtbuddy.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun OpenSourceLicensesScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    paddingValues: PaddingValues
) {
    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Open Source Licenses") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            windowInsets = WindowInsets.statusBars
        )
        var content by remember { mutableStateOf("") }

        val mdColors = DefaultMarkdownColors(
            text = MaterialTheme.colorScheme.onSurface,
            codeText = MaterialTheme.colorScheme.onSurface,
            inlineCodeText = MaterialTheme.colorScheme.onSurface,
            linkText = MaterialTheme.colorScheme.primary,
            codeBackground = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
            inlineCodeBackground = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
            dividerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        val mdTypography = DefaultMarkdownTypography(
            text = MaterialTheme.typography.bodyMedium,
            code = MaterialTheme.typography.bodyMedium.copy(fontFamily = MaterialTheme.typography.bodyMedium.fontFamily),
            inlineCode = MaterialTheme.typography.bodyMedium.copy(fontFamily = MaterialTheme.typography.bodyMedium.fontFamily),
            h1 = MaterialTheme.typography.titleLarge,
            h2 = MaterialTheme.typography.titleSmall,
            h3 = MaterialTheme.typography.titleSmall,
            h4 = MaterialTheme.typography.titleSmall,
            h5 = MaterialTheme.typography.titleSmall,
            h6 = MaterialTheme.typography.titleSmall,
            quote = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
            paragraph = MaterialTheme.typography.bodyMedium,
            ordered = MaterialTheme.typography.bodyMedium,
            bullet = MaterialTheme.typography.bodyMedium,
            list = MaterialTheme.typography.bodyMedium,
            link = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
        )

        LaunchedEffect(Unit) {
            content = Res.readBytes("files/open-source-licenses.md").decodeToString()
        }

        Markdown(
            content = content,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = paddingValues.calculateBottomPadding()),
            colors = mdColors,
            typography = mdTypography
        )
    }
}
