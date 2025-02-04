package com.example.fourthlab.ui.calculator3

import android.annotation.SuppressLint
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
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

// вхідні дані
data class Data(
    val rcn: Double = 0.0,
    val xcn: Double = 0.0,
    val rcmin: Double = 0.0,
    val xcmin: Double = 0.0,
)

// результати розрахунків
data class CalculationResults(
    val iThreeNormal: Int = 0,
    val iThreeMinimal: Int = 0,
    val iTwoNormal: Int = 0,
    val iTwoMinimal: Int = 0,

    val iThreeNormalActual: Int = 0,
    val iThreeMinimalActual: Int = 0,
    val iTwoNormalActual: Int = 0,
    val iTwoMinimalActual: Int = 0,

    val iThreeNormal10: Int = 0,
    val iThreeMinimal10: Int = 0,
    val iTwoNormal10: Int = 0,
    val iTwoMinimal10: Int = 0,
)

@Composable
fun Calculator3Screen(
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
            "Калькулятор визначення струмів для підстанції ХПнЕМ",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        InputField("Rc.н, Ом", data.rcn) { data = data.copy(rcn = it) }
        InputField("Хc.min, Ом", data.xcn) { data = data.copy(xcn = it) }
        InputField("Rc.н, Ом", data.rcmin) { data = data.copy(rcmin = it) }
        InputField("Хc.min, Ом", data.xcmin) { data = data.copy(xcmin = it) }

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
        ResultSection("Струми двофазного КЗ на шинах 10 кВ, приведені до напруги 110 кВ:") {
            ResultItem("нормальний режим", results.iTwoNormal, "А")
            ResultItem("мінімальний режим", results.iTwoMinimal, "А")
        }

        ResultSection("Струми трифазного КЗ на шинах 10 кВ, приведені до напруги 110 кВ:") {
            ResultItem("нормальний режим", results.iThreeNormal, "А")
            ResultItem("мінімальний режим", results.iThreeMinimal, "А")
        }

        ResultSection("Дійсні струми двофазного КЗ на шинах 10 кВ:") {
            ResultItem("нормальний режим", results.iTwoNormalActual, "А")
            ResultItem("мінімальний режим", results.iTwoMinimalActual, "А")
        }

        ResultSection("Дійсні струми трифазного КЗ на шинах 10 кВ:") {
            ResultItem("нормальний режим", results.iThreeNormalActual, "А")
            ResultItem("мінімальний режим", results.iThreeMinimalActual, "А")
        }

        ResultSection("Струми двофазного КЗ в точці 10:") {
            ResultItem("нормальний режим", results.iTwoNormal10, "А")
            ResultItem("мінімальний режим", results.iTwoMinimal10, "А")
        }

        ResultSection("Струми трифазного КЗ в точці 10:") {
            ResultItem("нормальний режим", results.iThreeNormal10, "А")
            ResultItem("мінімальний режим", results.iThreeMinimal10, "А")
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
fun ResultItem(label: String, value: Int, sign: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value.toString() + " " + sign)
    }
}

@SuppressLint("DefaultLocale")
private fun calculateResults(data: Data): CalculationResults {
    val ukmax = 11.1
    val uvn = 115
    val unn = 11
    val r0 = 0.64
    val x0 = 0.363
    // номінальна потужність трансформатора
    val snomt = 6.3

    // реактивний опір (реактанс) ТМН 6300/110
    val xt = Math.round((ukmax * uvn.toDouble().pow(2)) / (100 * snomt))

    // опори на шинах 10 кВ в нормальному та мінімальному режимах,
    // що приведені до напруги 110 кВ
    val rTire = data.rcn
    val xTire = data.xcn + xt
    val zTire = String.format("%.1f", (sqrt(rTire.pow(2) + xTire.pow(2)))).toDouble()
    val rTireMinimal = data.rcmin
    val xTireMinimal = data.xcmin + xt
    val zTireMinimal =
        String.format("%.1f", (sqrt(rTireMinimal.pow(2) + xTireMinimal.pow(2)))).toDouble()

    // Струми трифазного КЗ на шинах 10 кВ, приведені до напруги 110 кВ
    // (нормальний режим)
    val iThreeNormal = (uvn * 1000 / (1.73 * zTire)).roundToInt()
    // (мінімальний режим)
    val iThreeMinimal = (uvn * 1000 / (1.73 * zTireMinimal)).roundToInt()

    // Струми двофазного КЗ на шинах 10 кВ, приведені до напруги 110 кВ
    // (нормальний режим)
    val iTwoNormal = (iThreeNormal * (1.73 / 2)).roundToInt()
    // (мінімальний режим)
    val iTwoMinimal = (iThreeMinimal * (1.73 / 2)).roundToInt()

    // коефіцієнт приведення для визначення дійсних струмів на шинах 10 кВ
    val kpr = String.format("%.3f", unn.toDouble().pow(2) / uvn.toDouble().pow(2)).toDouble()

    // опори на шинах 10 кВ в нормальному та мінімальному режимах
    val rTireN = String.format("%.1f", rTire * kpr).toDouble()
    val xTireN = String.format("%.2f", xTire * kpr).toDouble()
    val zTireN = String.format("%.2f", (sqrt(rTireN.pow(2) + xTireN.pow(2)))).toDouble()
    val rTireNMinimal = String.format("%.2f", rTireMinimal * kpr).toDouble()
    val xTireNMinimal = String.format("%.2f", xTireMinimal * kpr).toDouble()
    val zTireNMinimal =
        String.format("%.1f", (sqrt(rTireNMinimal.pow(2) + xTireNMinimal.pow(2)))).toDouble()

    // Дійсні струми трифазного КЗ на шинах 10 кВ
    // (нормальний режим)
    val iThreeNormalActual = (unn * 1000 / (1.73 * zTireN)).roundToInt()
    // (мінімальний режим)
    val iThreeMinimalActual = (unn * 1000 / (1.73 * zTireNMinimal)).roundToInt()

    // Дійсні струми двофазного КЗ на шинах 10 кВ
    // (нормальний режим)
    val iTwoNormalActual = (iThreeNormalActual * (1.73 / 2)).roundToInt()
    // (мінімальний режим)
    val iTwoMinimalActual = (iThreeMinimalActual * (1.73 / 2)).roundToInt()

    // довжина лінії електропередач
    val il = 0.2 + 0.35 + 0.2 + 0.6 + 2 + 2.55 + 3.37 + 3.1
    // резистанс лінії електропередач
    val rl = il * r0
    // реактанс лінії електропередач
    val xl = il * x0

    // опори в точці 10 в нормальному та мінімальному режимах
    val r10n = String.format("%.2f", rl + rTireN).toDouble()
    val x10n = String.format("%.2f", xl + xTireN).toDouble()
    val z10n = String.format("%.2f", (sqrt(r10n.pow(2) + x10n.pow(2)))).toDouble()
    val r10nMinimal = String.format("%.2f", rl + rTireNMinimal).toDouble()
    val x10nMinimal = String.format("%.1f", xl + xTireNMinimal).toDouble()
    val z10nMinimal =
        String.format("%.2f", (sqrt(r10nMinimal.pow(2) + x10nMinimal.pow(2)))).toDouble()

    // Струми трифазного КЗ в точці 10
    // (нормальний режим)
    val iThreeNormal10 = (unn * 1000 / (1.73 * z10n)).roundToInt()
    // (мінімальний режим)
    val iThreeMinimal10 = (unn * 1000 / (1.73 * z10nMinimal)).roundToInt()

    // Струми двофазного КЗ в точці 10
    // (нормальний режим)
    val iTwoNormal10 = (iThreeNormal10 * (1.73 / 2)).roundToInt()
    // (мінімальний режим)
    val iTwoMinimal10 = (iThreeMinimal10 * (1.73 / 2)).roundToInt()

    return CalculationResults(
        iThreeNormal = iThreeNormal,
        iThreeMinimal = iThreeMinimal,
        iTwoNormal = iTwoNormal,
        iTwoMinimal = iTwoMinimal,
        iThreeNormalActual = iThreeNormalActual,
        iThreeMinimalActual = iThreeMinimalActual,
        iTwoNormalActual = iTwoNormalActual,
        iTwoMinimalActual = iTwoMinimalActual,
        iThreeNormal10 = iThreeNormal10,
        iThreeMinimal10 = iThreeMinimal10,
        iTwoNormal10 = iTwoNormal10,
        iTwoMinimal10 = iTwoMinimal10,
    )
}
