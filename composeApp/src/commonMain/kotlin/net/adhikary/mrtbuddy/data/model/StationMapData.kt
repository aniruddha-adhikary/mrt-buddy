package net.adhikary.mrtbuddy.data.model


data class MarkerInfo(
    val name: String,
    val lat: Double,
    val lon: Double,
)

object StationMapData {

    val depot = listOf(
        Pair(23.875330, 90.368135),
        Pair(23.876254, 90.368319),
        Pair(23.879849, 90.358013),
        Pair(23.877771, 90.355232),
        Pair(23.877571, 90.355215),
        Pair(23.877571, 90.355215),
        Pair(23.877302, 90.355341),
        Pair(23.877071, 90.355551),
        Pair(23.876948, 90.355846),
        Pair(23.875349, 90.368030),
    )

    val routes = listOf(
        Pair(23.877568, 90.359824),
        Pair(23.876343, 90.367242),
        Pair(23.876221, 90.367634),
        Pair(23.875732, 90.368262),
        Pair(23.875281, 90.368561),
        Pair(23.874791, 90.368601),
        Pair(23.867912, 90.367341),
        Pair(23.867097, 90.367031),
        Pair(23.866262, 90.366360),
        Pair(23.865747, 90.366091),
        Pair(23.860887, 90.365316),
        Pair(23.853781, 90.364229),
        Pair(23.846382, 90.363100),
        Pair(23.844108, 90.363260),
        Pair(23.844108, 90.363260),
        Pair(23.842757, 90.363355),
        Pair(23.840997, 90.363951),
        Pair(23.840496, 90.364096),
        Pair(23.839953, 90.364144),
        Pair(23.839373, 90.364120),
        Pair(23.838731, 90.363974),
        Pair(23.837406, 90.363566),
        Pair(23.829356, 90.363862),
        Pair(23.827455, 90.364206),
        Pair(23.822896, 90.364334),
        Pair(23.819320, 90.365213),
        Pair(23.812695, 90.366960),
        Pair(23.806717, 90.368657),
        Pair(23.798089, 90.372521),
        Pair(23.784445, 90.378252),
        Pair(23.776793, 90.380532),
        Pair(23.770610, 90.382522),
        Pair(23.765166, 90.383254),
        Pair(23.762875, 90.383375),
        Pair(23.761508, 90.383097),
        Pair(23.760655, 90.382973),
        Pair(23.760376, 90.382952),
        Pair(23.760134, 90.383003),
        Pair(23.759815, 90.383183),
        Pair(23.759508, 90.383430),
        Pair(23.759373, 90.383588),
        Pair(23.759176, 90.383907),
        Pair(23.759049, 90.384197),
        Pair(23.758950, 90.384712),
        Pair(23.758933, 90.385262),
        Pair(23.759080, 90.388260),
        Pair(23.759046, 90.388679),
        Pair(23.758928, 90.389033),
        Pair(23.758756, 90.389398),
        Pair(23.758412, 90.389789),
        Pair(23.758079, 90.390009),
        Pair(23.753836, 90.391678),
        Pair(23.753066, 90.391978),
        Pair(23.751357, 90.392740),
        Pair(23.748858, 90.393705),
        Pair(23.744939, 90.395057),
        Pair(23.742789, 90.395787),
        Pair(23.741207, 90.396119),
        Pair(23.740481, 90.396152),
        Pair(23.738094, 90.395948),
        Pair(23.736130, 90.395626),
        Pair(23.734018, 90.395583),
        Pair(23.733066, 90.395701),
        Pair(23.732712, 90.395851),
        Pair(23.732616, 90.395945),
        Pair(23.731696, 90.396860),
        Pair(23.728572, 90.399891),
        Pair(23.728332, 90.400330),
        Pair(23.728165, 90.400824),
        Pair(23.728111, 90.401232),
        Pair(23.728236, 90.403300),
        Pair(23.728341, 90.403715),
        Pair(23.728452, 90.403957),
        Pair(23.728638, 90.404225),
        Pair(23.729471, 90.405062),
        Pair(23.729726, 90.405365),
        Pair(23.729891, 90.405711),
        Pair(23.730011, 90.406301),
        Pair(23.730009, 90.407481),
        Pair(23.730043, 90.408672),
        Pair(23.730166, 90.410179),
        Pair(23.730149, 90.412231),
        Pair(23.730276, 90.414498),
        Pair(23.730188, 90.415163),
        Pair(23.730080, 90.415463),
        Pair(23.727300, 90.420291),
        Pair(23.726657, 90.421402),
        Pair(23.726672, 90.421815),
        Pair(23.727320, 90.422872),
        Pair(23.727840, 90.424508),
        Pair(23.728135, 90.425087),
        Pair(23.728503, 90.425323),
        Pair(23.732260, 90.425264),
    )

    val markers = listOf(
        MarkerInfo("Dhaka Metro Rail Depot, Diabari", 23.877568, 90.359824),
        MarkerInfo("Uttara North", 23.869147, 90.367491),
        MarkerInfo("Uttara Center", 23.859583, 90.365067),
        MarkerInfo("Uttara South", 23.845789, 90.363076),
        MarkerInfo("Pallabi", 23.826163, 90.364206),
        MarkerInfo("Mirpur 11", 23.819105, 90.365249),
        MarkerInfo("Mirpur 10", 23.808359, 90.368214),
        MarkerInfo("Kazipara", 23.799249, 90.371969),
        MarkerInfo("Shewrapara", 23.790966, 90.375476),
        MarkerInfo("Agargaon", 23.778439, 90.380049),
        MarkerInfo("Bijoy Sarani", 23.766569, 90.383082),
        MarkerInfo("Farmgate", 23.759056, 90.387059),
        MarkerInfo("Karwan Bazar", 23.751312, 90.392715),
        MarkerInfo("Shahbagh", 23.739303, 90.395976),
        MarkerInfo("Dhaka University", 23.731802, 90.396646),
        MarkerInfo("Bangladesh Secretariat", 23.730028, 90.407903),
        MarkerInfo("Motijheel", 23.728068, 90.419082),
        MarkerInfo("Kamalapur", 23.7319519, 90.4252219),
    )
}