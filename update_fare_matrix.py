import csv

# Read the CSV file
fare_matrix = {}
with open('/home/ubuntu/attachments/Dhaka_Metro_Rail_Fare_Structure_Updated.csv', 'r') as f:
    reader = csv.reader(f)
    stations = next(reader)[1:]  # Skip first column which contains station names

    for row in reader:
        from_station = row[0]
        fares = {}
        for i, fare in enumerate(row[1:]):  # Skip first column (station name)
            try:
                fare_value = int(fare)
                fares[stations[i]] = fare_value
            except ValueError:
                continue  # Skip non-numeric values
        fare_matrix[from_station] = fares

# Generate Kotlin code
kotlin_code = """package net.adhikary.mrtbuddy.model

object FareMatrix {
    private val matrix = mapOf(
"""

for from_station, fares in fare_matrix.items():
    # Replace "Secretariat" with "Bangladesh Secretariat"
    from_station_name = "Bangladesh Secretariat" if from_station == "Secretariat" else from_station
    kotlin_code += f'        "{from_station_name}" to mapOf(\n'

    fare_entries = []
    for station, fare in fares.items():
        # Replace "Secretariat" with "Bangladesh Secretariat" in destination
        to_station_name = "Bangladesh Secretariat" if station == "Secretariat" else station
        fare_entries.append(f'            "{to_station_name}" to {fare}')

    kotlin_code += ",\n".join(fare_entries)
    kotlin_code += "\n        ),\n"

kotlin_code += """    )

    fun getFare(from: String, to: String): Int {
        if (from.isEmpty() || to.isEmpty()) return 0

        // Try direct fare
        matrix[from]?.get(to)?.let { return it }
        // Try reverse direction (symmetric fares)
        matrix[to]?.get(from)?.let { return it }

        // If no fare found, return 0 (invalid combination)
        return 0
    }
}
"""

# Write to FareMatrix.kt
with open('app/src/main/java/net/adhikary/mrtbuddy/model/FareMatrix.kt', 'w') as f:
    f.write(kotlin_code)
