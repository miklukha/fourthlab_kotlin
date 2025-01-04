package com.example.fourthlab.ui.calculator2


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fourthlab.ui.theme.Purple40
import com.example.fourthlab.ui.theme.White


// дані збитків
data class LossesData(
    val lossesEmergencyDowntime: Double = 0.0,
    val lossesPlannedDowntime: Double = 0.0,
)

// результати розрахунків
data class CalculationResults(
    val mathExpectationLosses: Double = 0.0
)

@Composable
fun Calculator2Screen(
    goBack: () -> Unit,
) {
    var data by remember { mutableStateOf(LossesData()) }
    var results by remember { mutableStateOf<CalculationResults?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Калькулятор рахування збитків від перерв електропостачання",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        InputField("Збитки (аварійні вимкнення)", data.lossesEmergencyDowntime) {
            data = data.copy(lossesEmergencyDowntime = it)
        }
        InputField("Збитки (планові вимкнення)", data.lossesPlannedDowntime) {
            data = data.copy(lossesPlannedDowntime = it)
        }

        Button(
            onClick = { results = calculateResults(data) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .size(width = 300.dp, height = 50.dp),
        ) {
            Text("Розрахувати")
        }

        Button(
            onClick = goBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .size(width = 300.dp, height = 50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White),
            border = BorderStroke(2.dp, Purple40)
        ) {
            Text(
                text = "Повернутися",
                fontSize = 16.sp,
                color = Purple40
            )
        }

        results?.let { DisplayResults(it) }
    }
}

@Composable
private fun InputField(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit
) {
    OutlinedTextField(
        value = if (value == 0.0) "" else value.toString(),
        onValueChange = { onValueChange(it.toDoubleOrNull() ?: 0.0) },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
fun DisplayResults(results: CalculationResults) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            "Результати розрахунків:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ResultItem(
            "Математичне сподівання збитків\nвід переривання електропостачання:",
            results.mathExpectationLosses
        )

    }
}


@Composable
fun ResultItem(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(String.format("%.4f", value))
//        Text(Math.round(value).toString())
    }
}


private fun calculateResults(data: LossesData): CalculationResults {
    // частота відмов
    val failureRate = 0.01
    // середній час відновлення трансформатора напругою 35 кВ
    val recoveryTimeT = 45 * 0.001
    // середній час планового простою трансформатора напругою 35 кВ
    val averageTime = 4 * 0.001
    val pm = 5.12 * 1000
    val tm = 6451

    // математичне сподівання аварійного недовідпущення електроенергії
    val mathExpectationEmergency = failureRate * recoveryTimeT * pm * tm

    // математичне сподівання планового недовідпущення електроенергії
    val mathExpectationPlanned = averageTime * pm * tm

    // математичне сподівання збитків від переривання електропостачання
    val mathExpectationLosses =
        data.lossesEmergencyDowntime * mathExpectationEmergency +
                data.lossesPlannedDowntime * mathExpectationPlanned

    return CalculationResults(
        mathExpectationLosses = mathExpectationLosses
    )
}


