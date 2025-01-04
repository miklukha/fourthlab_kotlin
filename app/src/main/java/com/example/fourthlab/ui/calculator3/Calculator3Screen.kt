package com.example.fourthlab.ui.calculator3

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fourthlab.ui.theme.Purple40
import com.example.fourthlab.ui.theme.White

// дані систем
data class SystemData(
    val lengthPL110: Int = 0,
    val connectionN: Int = 0
)

// результати розрахунків
data class CalculationResults(
    val failureRate: Double = 0.0,
    val recoveryTime: Double = 0.0,
    val emergencyDowntime: Double = 0.0,
    val plannedDowntime: Double = 0.0,
    val failureRateBoth: Double = 0.0,
    val failureRateWithSection: Double = 0.0
)

@Composable
fun Calculator3Screen(
    goBack: () -> Unit,
) {
    var data by remember { mutableStateOf(SystemData()) }
    var results by remember { mutableStateOf<CalculationResults?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Калькулятор порівняння надійності систем електропередачі",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Одноколова система містить: елегазовий вимикач 100 кВ, ПЛ-110 кВ довжиною 10 км, трансформатор 110/10 кВ, ввідний вимикач 10 кВ і 6 приєднань 10 кВ.",
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Text(
            "Двоколова система складається з двох ідентичних одноколових і секційного вимикача 10 кВ.",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        InputField("Довжина ПЛ-110 кВ, км", data.lengthPL110) { data = data.copy(lengthPL110 = it) }
        InputField("Кількість приєднань 10 кВ", data.connectionN) {
            data = data.copy(connectionN = it)
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
fun InputField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    OutlinedTextField(
        value = if (value == 0) "" else value.toString(),
        onValueChange = {
            onValueChange(it.toInt())
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
        ResultItem("Середня тривалість відновлення", results.recoveryTime, "год")

        ResultSection("Частота відмов:") {
            ResultItem("одноколової системи", results.failureRate, "рік-1")
            ResultItem("одночасно двох кіл \nдвоколової системи", results.failureRateBoth, "рік-1")
            ResultItem(
                "двоколової системи з \n(секційний вимикач)",
                results.failureRateWithSection,
                "рік-1"
            )
        }

        ResultSection("Кофіцієнт простою одноколової системи:") {
            ResultItem("аварійного:", results.emergencyDowntime, "")
            ResultItem("планового:", results.plannedDowntime, "")
        }

    }
}

@Composable
fun ResultSection(title: String, content: @Composable () -> Unit) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
    content()
}

@Composable
fun ResultItem(label: String, value: Double, sign: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(String.format("%.4f", value) + " " + sign)
//        Text(value.toString() + " " + sign)

    }
}

private fun calculateResults(data: SystemData): CalculationResults {
    // частоти відмов
    val failureRateV110 = 0.01 // В-110 кВ (елегазовий)
    val failureRateV10 = 0.02 // В-10 кВ (малооливний)
    val failureRateT110 = 0.015 // Т-110 кВ
    val failureRatePL110 = 0.007 * data.lengthPL110 // ПЛ-110 кВ
    val failureRate10 = 0.03 * data.connectionN // збірні шини 10кВ

    // тривалості відновлення
    val recoveryTimeV110 = 30 // В-110 кВ (елегазовий)
    val recoveryTimeV10 = 15 // В-10 кВ (малооливний)
    val recoveryTimeT110 = 100 // Т-110 кВ
    val recoveryTimePL110 = 10 // ПЛ-110 кВ
    val recoveryTime10 = 2 // збірні шини 10кВ

    // найбільше значення коефіцієнта планового простою (в даному випадку для Т-110 кВ)
    val plannedDowntimeMax = 43

    // частота відмов одноколової системи - сума частот відмов одноколової системи
    val failureRate =
        failureRateV110 + failureRateV10 + failureRateT110 + failureRatePL110 + failureRate10

    // середня тривалість відновлення
    val recoveryTime =
        (failureRateV110 * recoveryTimeV110 + failureRatePL110 * recoveryTimePL110 +
                failureRateT110 * recoveryTimeT110 + failureRateV10 * recoveryTimeV10 +
                failureRate10 * recoveryTime10) / failureRate

    // кофіцієнт аварійного простою одноколової системи
    val emergencyDowntime = failureRate * recoveryTime / 8760

    // кофіцієнт планового простою одноколової системи
    val plannedDowntime = 1.2 * plannedDowntimeMax / 8760

    // частота відмов одночасно двох кіл двоколової системи
    val failureRateBoth = 2 * failureRate * (emergencyDowntime + plannedDowntime)

    // частота відмов двоколової системи з урахуванням секційного вимикача
    val failureRateWithSection = failureRateBoth + failureRateV10

    return CalculationResults(
        failureRate = failureRate,
        recoveryTime = recoveryTime,
        emergencyDowntime = emergencyDowntime,
        plannedDowntime = plannedDowntime,
        failureRateBoth = failureRateBoth,
        failureRateWithSection = failureRateWithSection,
    )
}
