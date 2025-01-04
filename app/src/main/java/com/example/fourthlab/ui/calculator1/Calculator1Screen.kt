package com.example.fourthlab.ui.calculator1

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
import kotlin.math.sqrt

// дані систем
data class SystemData(
    val current: Double = 0.0,
    val switchOffCurrentTime: Double = 0.0,
    val sm: Double = 0.0,
)

// результати розрахунків
data class CalculationResults(
    val section: Double = 0.0,
    val increaseMinimum: Double = 0.0
)

@Composable
fun Calculator1Screen(
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
            "Калькулятор для розрахунку струму ",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Вибрати кабелі для живлення двотрансформаторної підстанції системи внутрішнього елкетропостачання підприємства напругою 10 кВ. Струм К3 Ік = 2.5 кА, фіктиіний час вимикання струму КЗ tф = 2.5с. Потужність ТП - 2х1000 кВ А. Розрахункове навантаження Sм = 1300 кВ А, Тм = 4000 год.",
            modifier = Modifier.padding(bottom = 10.dp)
        )

        InputField("Струм КЗ, кА", data.current) { data = data.copy(current = it) }
        InputField("Фіктивний час вимикання струму КЗ, с", data.switchOffCurrentTime) {
            data = data.copy(switchOffCurrentTime = it)
        }
        InputField("Sm, кВ", data.sm) { data = data.copy(sm = it) }

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
        ResultItem("Економічний переріз", results.section, "мм2")
        ResultItem("Переріз має бути збільшений мінімум до", results.increaseMinimum, "мм2")
    }
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
        Text(String.format("%.1f", value) + " " + sign)
//        Text(value.toString() + " " + sign)

    }
}

private fun calculateResults(data: SystemData): CalculationResults {
    // напруга
    val voltage = 10
    // економічна густина струму для кабелів з паперовою ізоляцією для Тм = 4000год
    val density = 1.4
    // для кабелів з алюмінієвими суцільними жилами,
    // паперовою ізоляцією і номінальною напругою 6 кВ
    val ct = 92

    // розрахунковий струм для нормального режиму
    val ratedCurrentNormal = (data.sm / 2) / (sqrt(3.0) * voltage)

    // розрахунковий струм для післяаварійного режиму
    val ratedCurrentAfterEmergency = 2 * ratedCurrentNormal

    // економічний переріз
    val section = ratedCurrentNormal / density

    // мінімум збільшення перерізу жил кабелю
    val increaseMinimum = data.current * 1000 * sqrt(data.switchOffCurrentTime) / ct

    return CalculationResults(
        section = section,
        increaseMinimum = increaseMinimum,
    )
}

