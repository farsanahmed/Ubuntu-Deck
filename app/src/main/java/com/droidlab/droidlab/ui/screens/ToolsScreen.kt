package com.droidlab.droidlab.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.droidlab.droidlab.R

@Composable
fun ToolsScreen(context: Context = android.context) {
    TabRow(
        selectedTabIndex = selectedTabIndex.value,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        divider = {}
    ) {
        Tab(
            text = { Text("JSON") },
            icon = { Icon(Icons.Default.Code, contentDescription = null) },
            selected = selectedTabIndex.value == 0,
            onClick = { selectedTabIndex.value = 0 }
        )
        Tab(
            text = { Text("Base64") },
            icon = { Icon(Icons.Default.FontDownload, contentDescription = null) },
            selected = selectedTabIndex.value == 1,
            onClick = { selectedTabIndex.value = 1 }
        )
        Tab(
            text = { Text("SHA-256") },
            icon = { Icon(Icons.Default.Security, contentDescription = null) },
            selected = selectedTabIndex.value == 2,
            onClick = { selectedTabIndex.value = 2 }
        )
        Tab(
            text = { Text("Unit Converter") },
            icon = { Icon(Icons.Default.Science, contentDescription = null) },
            selected = selectedTabIndex.value == 3,
            onClick = { selectedTabIndex.value = 3 }
        )
    }

    when (selectedTabIndex.value) {
        0 -> JsonTool()
        1 -> Base64Tool()
        2 -> HashTool()
        3 -> UnitConverterTool()
    }
}

class TabIndex(var value: Int)
val selectedTabIndex: State<TabIndex> = remember { TabIndex(0) }

@Composable
private fun JsonTool() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "JSON Formatter & Validator",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        var jsonInput by remember { mutableStateOf("") }
        var jsonOutput by remember { mutableStateOf("") }
        var isValid by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf("") }

        OutlinedTextField(
            label = { Text("Input JSON") },
            value = jsonInput,
            onValueChange = { jsonInput = it },
            placeholder = { Text("Enter JSON here...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 10
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    try {
                        // Validate and format JSON using Gson
                        val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
                        val jsonElement = com.google.gson.JsonParser.parseString(jsonInput)
                        jsonOutput = gson.toJson(jsonElement)
                        isValid = true
                        errorMessage = ""
                    } catch (e: Exception) {
                        isValid = false
                        errorMessage = e.message ?: "Invalid JSON"
                        jsonOutput = ""
                    }
                },
                enabled = jsonInput.isNotEmpty()
            ) {
                Text("Format")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    jsonInput = ""
                    jsonOutput = ""
                    isValid = true
                    errorMessage = ""
                }
            ) {
                Text("Clear")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (!isValid) {
            Text(
                text = "Error: $errorMessage",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = "Output:",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = jsonOutput,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isValid) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
                .maxHeight(200.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun Base64Tool() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Base64 Encoder/Decoder",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        var input by remember { mutableStateOf("") }
        var output by remember { mutableStateOf("") }
        var mode by remember { mutableStateOf(true) } // true = encode, false = decode

        OutlinedTextField(
            label = { Text("Input") },
            value = input,
            onValueChange = { input = it },
            placeholder = { Text("Enter text to encode or Base64 to decode...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            ToggleChip(
                label = { Text("Encode") },
                selected = mode,
                onClick = { mode = true }
            )
            ToggleChip(
                label = { Text("Decode") },
                selected = !mode,
                onClick = { mode = false }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (mode) {
                        output = android.util.Base64.encodeToString(input.toByteArray(android.nio.charset.StandardCharsets.UTF_8), android.util.Base64.NO_WRAP)
                    } else {
                        try {
                            val decodedBytes = android.util.Base64.decode(input, android.util.Base64.NO_WRAP)
                            output = String(decodedBytes, android.nio.charset.StandardCharsets.UTF_8)
                        } catch (e: Exception) {
                            output = "Invalid Base64 input"
                        }
                    }
                },
                enabled = input.isNotEmpty()
            ) {
                Text(if (mode) "Encode" else "Decode")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    input = ""
                    output = ""
                }
            ) {
                Text("Clear")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Output:",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = output,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
                .maxHeight(100.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun HashTool() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "SHA-256 Hasher",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        var input by remember { mutableStateOf("") }
        var hash by remember { mutableStateOf("") }

        OutlinedTextField(
            label = { Text("Input") },
            value = input,
            onValueChange = { input = it },
            placeholder = { Text("Enter text to hash...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val digest = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray(android.nio.charset.StandardCharsets.UTF_8))
                    hash = digest.joinToString("") { "%02x".format(it) }
                },
                enabled = input.isNotEmpty()
            ) {
                Text("Hash")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    input = ""
                    hash = ""
                }
            ) {
                Text("Clear")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SHA-256 Hash:",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = hash,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
                .maxHeight(100.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun UnitConverterTool() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Unit Converter",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        var inputValue by remember { mutableStateOf("") }
        var convertedValue by remember { mutableStateOf("") }
        var fromUnit by remember { mutableStateOf("Meter") }
        var toUnit by remember { mutableStateOf("Kilometer") }
        var conversionType by remember { mutableStateOf("Length") }

        val unitsMap = mapOf(
            "Length" to listOf("Meter", "Kilometer", "Centimeter", "Millimeter", "Mile", "Yard", "Foot", "Inch"),
            "Weight" to listOf("Gram", "Kilogram", "Milligram", "Pound", "Ounce"),
            "Temperature" to listOf("Celsius", "Fahrenheit", "Kelvin"),
            "Volume" to listOf("Liter", "Milliliter", "Gallon", "Quart", "Pint", "Cup")
        )

        OutlinedTextField(
            label = { Text("Value") },
            value = inputValue,
            onValueChange = { inputValue = it },
            placeholder = { Text("Enter value to convert") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DropdownMenuBox(
                expanded = expandedFrom.value,
                onDismissRequest = { expandedFrom.value = false }
            ) {
                val expanded = expandedFrom.value
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expandedFrom.value = false }
                ) {
                    unitsMap[conversionType]?.forEach { unit ->
                        DropdownMenuItem(
                            onClick = {
                                fromUnit = unit
                                expandedFrom.value = false
                                convert()
                            }
                        ) {
                            Text(unit)
                        }
                    }
                }
                Text(
                    text = fromUnit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedFrom.value = !expandedFrom.value }
                        .padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.CompareArrows,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuBox(
                expanded = expandedTo.value,
                onDismissRequest = { expandedTo.value = false }
            ) {
                val expanded = expandedTo.value
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expandedTo.value = false }
                ) {
                    unitsMap[conversionType]?.forEach { unit ->
                        DropdownMenuItem(
                            onClick = {
                                toUnit = unit
                                expandedTo.value = false
                                convert()
                            }
                        ) {
                            Text(unit)
                        }
                    }
                }
                Text(
                    text = toUnit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedTo.value = !expandedTo.value }
                        .padding(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        DropdownMenuBox(
            expanded = expandedType.value,
            onDismissRequest = { expandedType.value = false }
        ) {
            val expanded = expandedType.value
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expandedType.value = false }
            ) {
                unitsMap.keys.forEach { type ->
                    DropdownMenuItem(
                        onClick = {
                            conversionType = type
                            // Reset units when type changes
                            fromUnit = unitsMap[type]?.first() ?: ""
                            toUnit = unitsMap[type]?.get(1) ?: unitsMap[type]?.first() ?: ""
                            expandedType.value = false
                            convert()
                        }
                    ) {
                        Text(type)
                    }
                }
            }
            Text(
                text = conversionType,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedType.value = !expandedType.value }
                    .padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    convert()
                },
                enabled = inputValue.isNotBlank()
            ) {
                Text("Convert")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    inputValue = ""
                    convertedValue = ""
                    fromUnit = "Meter"
                    toUnit = "Kilometer"
                    conversionType = "Length"
                }
            ) {
                Text("Reset")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (convertedValue.isNotEmpty()) {
            Text(
                text = "$inputValue $fromUnit = $convertedValue $toUnit",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(align = androidx.compose.ui.Alignment.CenterHorizontally)
            )
        }
    }

    var expandedType by remember { mutableStateOf(false) }
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }

    private fun convert() {
        if (inputValue.isBlank()) {
            convertedValue = ""
            return
        }
        val value = inputValue.toDoubleOrNull() ?: return
        convertedValue = when (conversionType) {
            "Length" -> convertLength(value, fromUnit, toUnit)
            "Weight" -> convertWeight(value, fromUnit, toUnit)
            "Temperature" -> convertTemperature(value, fromUnit, toUnit)
            "Volume" -> convertVolume(value, fromUnit, toUnit)
            else -> "0"
        }
    }

    private fun convertLength(value: Double, from: String, to: String): String {
        val inMeters = when (from) {
            "Meter" -> value
            "Kilometer" -> value * 1000
            "Centimeter" -> value / 100
            "Millimeter" -> value / 1000
            "Mile" -> value * 1609.34
            "Yard" -> value * 0.9144
            "Foot" -> value * 0.3048
            "Inch" -> value * 0.0254
            else -> 0.0
        }
        return when (to) {
            "Meter" -> inMeters
            "Kilometer" -> inMeters / 1000
            "Centimeter" -> inMeters * 100
            "Millimeter" -> inMeters * 1000
            "Mile" -> inMeters / 1609.34
            "Yard" -> inMeters / 0.9144
            "Foot" -> inMeters / 0.3048
            "Inch" -> inMeters / 0.0254
            else -> 0.0
        }.toString()
    }

    private fun convertWeight(value: Double, from: String, to: String): String {
        val inGrams = when (from) {
            "Gram" -> value
            "Kilogram" -> value * 1000
            "Milligram" -> value / 1000
            "Pound" -> value * 453.592
            "Ounce" -> value * 28.3495
            else -> 0.0
        }
        return when (to) {
            "Gram" -> inGrams
            "Kilogram" -> inGrams / 1000
            "Milligram" -> inGrams * 1000
            "Pound" -> inGrams / 453.592
            "Ounce" -> inGrams / 28.3495
            else -> 0.0
        }.toString()
    }

    private fun convertTemperature(value: Double, from: String, to: String): String {
        val inCelsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32) * 5 / 9
            "Kelvin" -> value - 273.15
            else -> 0.0
        }
        return when (to) {
            "Celsius" -> inCelsius
            "Fahrenheit" -> inCelsius * 9 / 5 + 32
            "Kelvin" -> inCelsius + 273.15
            else -> 0.0
        }.toString()
    }

    private fun convertVolume(value: Double, from: String, to: String): String {
        val inLiters = when (from) {
            "Liter" -> value
            "Milliliter" -> value / 1000
            "Gallon" -> value * 3.78541
            "Quart" -> value * 0.946353
            "Pint" -> value * 0.473176
            "Cup" -> value * 0.24
            else -> 0.0
        }
        return when (to) {
            "Liter" -> inLiters
            "Milliliter" -> inLiters * 1000
            "Gallon" -> inLiters / 3.78541
            "Quart" -> inLiters / 0.946353
            "Pint" -> inLiters / 0.473176
            "Cup" -> inLiters / 0.24
            else -> 0.0
        }.toString()
    }
}
