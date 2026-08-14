package com.example.ui.screens.academics.components

import androidx.compose.runtime.Composable
import com.example.ui.screens.academics.models.Program
import com.example.ui.screens.programs.ProgramsScreen as UnifiedProgramsScreen

@Composable
fun ProgramsScreen(
    onBack: () -> Unit,
    onProgramSelect: (Program) -> Unit = {}
) {
    UnifiedProgramsScreen(onBack = onBack)
}
