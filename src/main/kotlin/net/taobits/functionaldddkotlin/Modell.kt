package net.taobits.functionaldddkotlin

data class KundeId(val name: String)
data class WarenkorbId(val id: Int)
data class ProduktId(val id: Int)


abstract class Command
abstract class WarenkorbCommand(open val warenkorb: WarenkorbId): Command()
data class LegeWarenkorbAn(override val warenkorb: WarenkorbId, val kunde: KundeId): WarenkorbCommand(warenkorb)
data class ProduktInWarenkorbLegen(override val warenkorb: WarenkorbId, val produkt: ProduktId) : WarenkorbCommand(warenkorb)
data class BestelleWarenkorb(override val warenkorb: WarenkorbId) : WarenkorbCommand(warenkorb)

abstract class Event
abstract class WarenkorbEvent(open val warenkorb: WarenkorbId): Event()
data class WarenkorbWurdeAngelegt(override val warenkorb: WarenkorbId, val kunde: KundeId): WarenkorbEvent(warenkorb)
data class ProduktWurdeInWarenkorbGelegt(override val warenkorb: WarenkorbId, val produkt: ProduktId): WarenkorbEvent(warenkorb)
data class WarenkorbWurdeBestellt(override val warenkorb: WarenkorbId): WarenkorbEvent(warenkorb)