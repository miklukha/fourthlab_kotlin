package com.example.fourthlab.ui.calculator2


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
import kotlin.math.pow
import kotlin.math.sqrt


// вхідні дані
data class Data(
    val power: Int = 0,
)

// результати розрахунків
data class CalculationResults(
    val initialCurrentValue: Double = 0.0
)

@Composable
fun Calculator2Screen(
    goBack: () -> Unit,
) {
    var data by remember { mutableStateOf(Data()) }
    var results by remember { mutableStateOf<CalculationResults?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Калькулятор визначення струмів КЗ на шинах 10 кВ ГПП",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Визначити струми К3 на шинах 10 кВ ГПП. Потужність К3 200 МВ А. Для перевірки вибраних кабелів та вимикачів необіхдно розрахувати струми К3 на шинах низької напруги ГПП",
            modifier = Modifier.padding(bottom = 10.dp)
        )

        InputField("Потужність КЗ", data.power) {
            data = data.copy(power = it)
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
        ResultItem(
            "Початкове діюче значення \nструму трифазного КЗ:",
            results.initialCurrentValue, "кА"
        )

    }
}


@Composable
fun ResultItem(label: String, value: Double, sign: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(String.format("%.1f", value) + " " + sign)
//        Text(Math.round(value).toString())
    }
}


private fun calculateResults(data: Data): CalculationResults {
    // середня номінальна напруга точки, в якій виникає КЗ
    val usn = 10.5
    // номінальна потужність трансформатора
    val snomt = 6.3
    val uk = 10.5


    // середній час відновлення трансформатора напругою 35 кВ
    val recoveryTimeT = 45 * 0.001
    // середній час планового простою трансформатора напругою 35 кВ
    val averageTime = 4 * 0.001
    val pm = 5.12 * 1000
    val tm = 6451

    // опори елементів заступної схеми
    val xc = usn.pow(2) / data.power
    val xt = (uk / 100) * (usn.pow(2) / snomt)

    // сумарний опір для точки К1
    val totalResistance = xc + xt

    // початкове діюче значення струму трифазного КЗ
    val initialCurrentValue = usn / (sqrt(3.0) * totalResistance)

    return CalculationResults(
        initialCurrentValue = initialCurrentValue
    )
}


