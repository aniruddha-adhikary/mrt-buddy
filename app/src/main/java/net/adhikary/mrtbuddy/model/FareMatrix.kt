package net.adhikary.mrtbuddy.model

object FareMatrix {
    private val matrix = mapOf(
        // Keeping existing entries for Uttara North, Center, and South
        "Uttara North" to mapOf(
            "Uttara Center" to 20, "Uttara South" to 20, "Pallabi" to 30,
            "Mirpur-11" to 30, "Mirpur-10" to 40, "Kazipara" to 40,
            "Shewrapara" to 40, "Agargaon" to 60, "Bijoy Sarani" to 60,
            "Farmgate" to 70, "Karwan Bazar" to 70, "Shahbagh" to 80,
            "Dhaka University" to 80, "Secretariat" to 100, "Motijheel" to 100
        ),
        "Uttara Center" to mapOf(
            "Uttara South" to 20, "Pallabi" to 30, "Mirpur-11" to 30,
            "Mirpur-10" to 40, "Kazipara" to 40, "Shewrapara" to 40,
            "Agargaon" to 60, "Bijoy Sarani" to 60, "Farmgate" to 70,
            "Karwan Bazar" to 70, "Shahbagh" to 80, "Dhaka University" to 80,
            "Secretariat" to 100, "Motijheel" to 100
        ),
        "Uttara South" to mapOf(
            "Pallabi" to 30, "Mirpur-11" to 30, "Mirpur-10" to 40,
            "Kazipara" to 40, "Shewrapara" to 40, "Agargaon" to 60,
            "Bijoy Sarani" to 60, "Farmgate" to 70, "Karwan Bazar" to 70,
            "Shahbagh" to 80, "Dhaka University" to 80, "Secretariat" to 100,
            "Motijheel" to 100
        ),
        // Adding new entries for Pallabi through Agargaon
        "Pallabi" to mapOf(
            "Mirpur-11" to 20, "Mirpur-10" to 20, "Kazipara" to 20,
            "Shewrapara" to 20, "Agargaon" to 40, "Bijoy Sarani" to 40,
            "Farmgate" to 50, "Karwan Bazar" to 50, "Shahbagh" to 60,
            "Dhaka University" to 60, "Secretariat" to 80, "Motijheel" to 80
        ),
        "Mirpur-11" to mapOf(
            "Mirpur-10" to 20, "Kazipara" to 20, "Shewrapara" to 20,
            "Agargaon" to 40, "Bijoy Sarani" to 40, "Farmgate" to 50,
            "Karwan Bazar" to 50, "Shahbagh" to 60, "Dhaka University" to 60,
            "Secretariat" to 80, "Motijheel" to 80
        ),
        "Mirpur-10" to mapOf(
            "Kazipara" to 20, "Shewrapara" to 20, "Agargaon" to 40,
            "Bijoy Sarani" to 40, "Farmgate" to 50, "Karwan Bazar" to 50,
            "Shahbagh" to 60, "Dhaka University" to 60, "Secretariat" to 80,
            "Motijheel" to 80
        ),
        "Kazipara" to mapOf(
            "Shewrapara" to 20, "Agargaon" to 40, "Bijoy Sarani" to 40,
            "Farmgate" to 50, "Karwan Bazar" to 50, "Shahbagh" to 60,
            "Dhaka University" to 60, "Secretariat" to 80, "Motijheel" to 80
        ),
        "Shewrapara" to mapOf(
            "Agargaon" to 40, "Bijoy Sarani" to 40, "Farmgate" to 50,
            "Karwan Bazar" to 50, "Shahbagh" to 60, "Dhaka University" to 60,
            "Secretariat" to 80, "Motijheel" to 80
        ),
        "Agargaon" to mapOf(
            "Bijoy Sarani" to 20, "Farmgate" to 30, "Karwan Bazar" to 30,
            "Shahbagh" to 40, "Dhaka University" to 40, "Secretariat" to 60,
            "Motijheel" to 60
        ),
        "Bijoy Sarani" to mapOf(
            "Farmgate" to 30, "Karwan Bazar" to 30, "Shahbagh" to 40,
            "Dhaka University" to 40, "Secretariat" to 60, "Motijheel" to 60
        ),
        "Farmgate" to mapOf(
            "Karwan Bazar" to 20, "Shahbagh" to 30, "Dhaka University" to 30,
            "Secretariat" to 50, "Motijheel" to 50
        ),
        "Karwan Bazar" to mapOf(
            "Shahbagh" to 30, "Dhaka University" to 30,
            "Secretariat" to 50, "Motijheel" to 50
        ),
        "Shahbagh" to mapOf(
            "Dhaka University" to 20,
            "Secretariat" to 40, "Motijheel" to 40
        ),
        "Dhaka University" to mapOf(
            "Secretariat" to 40, "Motijheel" to 40
        ),
        "Secretariat" to mapOf(
            "Motijheel" to 20
        )
    )

    fun getFare(from: String, to: String): Int {
        if (from.isEmpty() || to.isEmpty()) return 0
        if (from == to) return 20

        // Try direct fare
        matrix[from]?.get(to)?.let { return it }
        // Try reverse direction (symmetric fares)
        matrix[to]?.get(from)?.let { return it }

        // If no fare found, return 0 (invalid combination)
        return 0
    }
}
