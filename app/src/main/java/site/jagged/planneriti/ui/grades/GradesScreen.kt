package site.jagged.planneriti.ui.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import site.jagged.planneriti.ui.components.AppCard
import site.jagged.planneriti.ui.components.AppSectionTitle
import site.jagged.planneriti.ui.theme.OnSurfaceMuted
import site.jagged.planneriti.ui.theme.Primary
import site.jagged.planneriti.ui.theme.Background

@Composable
fun GradesScreen(viewModel: GradesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppSectionTitle("Grades", "Your identity data for grade sync lives here")

        AppCard {
            AppSectionTitle("IDNP", "Stored securely and masked everywhere else")
            OutlinedTextField(
                value = uiState.idnpInput,
                onValueChange = viewModel::onIdnpChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("IDNP (13 digits)") },
                singleLine = true,
                supportingText = { Text("Only digits are accepted.") }
            )
            Text("Current value: ${uiState.savedIdnpMasked}", color = OnSurfaceMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::saveIdnp, enabled = !uiState.isSaving) {
                    Text(if (uiState.isSaving) "Saving..." else "Save IDNP")
                }
                OutlinedButton(onClick = viewModel::clearIdnp, enabled = !uiState.isSaving) {
                    Text("Clear")
                }
            }
            uiState.message?.let { Text(it, color = Color(0xFF30D158), fontWeight = FontWeight.SemiBold) }
            uiState.error?.let { Text(it, color = Color(0xFFFF8B8B), fontWeight = FontWeight.SemiBold) }
        }

        AppCard {
            Text("Grades list is coming next.", color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                "This space is prepared for modern grade cards, filters, and trend indicators.",
                color = OnSurfaceMuted
            )
            Spacer(Modifier.height(6.dp))
            Text("Primary actions and text use high-contrast app colors.", color = Primary)
        }
    }
}