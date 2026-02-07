package com.omnitool.features.converter.fuel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Fuel Calculator ViewModel
 * 
 * Features:
 * - Calculate fuel consumption
 * - Calculate trip cost
 * - Multiple fuel efficiency units
 */
@HiltViewModel
class FuelCalculatorViewModel @Inject constructor() : ViewModel() {
    
    var distance by mutableStateOf("")
        private set
    
    var fuelUsed by mutableStateOf("")
        private set
    
    var fuelPrice by mutableStateOf("")
        private set
    
    var distanceUnit by mutableStateOf(DistanceUnit.KM)
        private set
    
    var fuelUnit by mutableStateOf(FuelVolumeUnit.LITERS)
        private set
    
    // Results
    var fuelEfficiency by mutableStateOf<String?>(null)
        private set
    
    var fuelEfficiencyInverse by mutableStateOf<String?>(null)
        private set
    
    var tripCost by mutableStateOf<String?>(null)
        private set
    
    var costPerKm by mutableStateOf<String?>(null)
        private set
    
    fun setDistance(value: String) {
        distance = value.filter { it.isDigit() || it == '.' }
        calculate()
    }
    
    fun setFuelUsed(value: String) {
        fuelUsed = value.filter { it.isDigit() || it == '.' }
        calculate()
    }
    
    fun setFuelPrice(value: String) {
        fuelPrice = value.filter { it.isDigit() || it == '.' }
        calculate()
    }
    
    fun setDistanceUnit(unit: DistanceUnit) {
        distanceUnit = unit
        calculate()
    }
    
    fun setFuelUnit(unit: FuelVolumeUnit) {
        fuelUnit = unit
        calculate()
    }
    
    private fun calculate() {
        val dist = distance.toDoubleOrNull()
        val fuel = fuelUsed.toDoubleOrNull()
        val price = fuelPrice.toDoubleOrNull()
        
        if (dist == null || fuel == null || dist <= 0 || fuel <= 0) {
            fuelEfficiency = null
            fuelEfficiencyInverse = null
            tripCost = null
            costPerKm = null
            return
        }
        
        // Convert to km and liters for calculations
        val distKm = when (distanceUnit) {
            DistanceUnit.KM -> dist
            DistanceUnit.MILES -> dist * 1.60934
        }
        
        val fuelLiters = when (fuelUnit) {
            FuelVolumeUnit.LITERS -> fuel
            FuelVolumeUnit.GALLONS_US -> fuel * 3.78541
            FuelVolumeUnit.GALLONS_UK -> fuel * 4.54609
        }
        
        // L/100km
        val litersPer100Km = (fuelLiters / distKm) * 100
        fuelEfficiency = String.format("%.2f L/100km", litersPer100Km)
        
        // km/L
        val kmPerLiter = distKm / fuelLiters
        fuelEfficiencyInverse = String.format("%.2f km/L", kmPerLiter)
        
        // MPG (US)
        val mpgUs = (dist * if (distanceUnit == DistanceUnit.MILES) 1.0 else 0.621371) / 
                   (fuel * if (fuelUnit == FuelVolumeUnit.GALLONS_US) 1.0 else 
                    when (fuelUnit) {
                        FuelVolumeUnit.LITERS -> 0.264172
                        FuelVolumeUnit.GALLONS_UK -> 1.20095
                        else -> 1.0
                    })
        
        if (price != null && price > 0) {
            tripCost = String.format("%.2f", fuelLiters * price)
            costPerKm = String.format("%.3f/km", (fuelLiters * price) / distKm)
        } else {
            tripCost = null
            costPerKm = null
        }
    }
    
    fun clear() {
        distance = ""
        fuelUsed = ""
        fuelPrice = ""
        fuelEfficiency = null
        fuelEfficiencyInverse = null
        tripCost = null
        costPerKm = null
    }
}

enum class DistanceUnit(val displayName: String, val symbol: String) {
    KM("Kilometers", "km"),
    MILES("Miles", "mi")
}

enum class FuelVolumeUnit(val displayName: String, val symbol: String) {
    LITERS("Liters", "L"),
    GALLONS_US("Gallons (US)", "gal"),
    GALLONS_UK("Gallons (UK)", "gal UK")
}
