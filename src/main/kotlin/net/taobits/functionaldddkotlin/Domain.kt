package net.taobits.functionaldddkotlin

import java.lang.IllegalArgumentException
import java.lang.RuntimeException

data class State(val warenkorb: WarenkorbId? = null,
                 val produkte: List<ProduktId> = emptyList(),
                 val warenkorbOffen: Boolean = true,
                 val kunde: KundeId? = null)

fun handle(command: Command, state: State): List<Event> =
    when(command) {
        is LegeWarenkorbAn -> warenkorbAnlegen(state, command.warenkorb, command.kunde)
        is BestelleWarenkorb -> bestelleWarenkorb(state, command.warenkorb)
        is ProduktInWarenkorbLegen -> produktInWarenkorbLegen(state, command.warenkorb, command.produkt)
        else -> throw IllegalArgumentException("Unkown command $command")
    }

fun warenkorbAnlegen(state: State, warenkorb: WarenkorbId, kunde: KundeId): List<Event> =
        listOf(WarenkorbWurdeAngelegt(warenkorb, kunde))

fun  produktInWarenkorbLegen(state: State, warenkorb: WarenkorbId, produkt: ProduktId): List<Event> =
    if (state.warenkorb == null) throw HandleCommandException("Warenkorb muss angelegt sein")
    else if (! state.warenkorbOffen) throw HandleCommandException("Warenkorb schon bestellt")
    else listOf(ProduktWurdeInWarenkorbGelegt(warenkorb, produkt))

fun bestelleWarenkorb(state: State, warenkorb: WarenkorbId): List<Event> =
    if (state.warenkorb == null) throw HandleCommandException("Warenkorb muss angelegt sein")
    else if (state.produkte.isEmpty()) throw HandleCommandException("Warenkorb darf nicht leer sein")
    else listOf(WarenkorbWurdeBestellt(warenkorb))

class HandleCommandException(message: String): RuntimeException(message)

fun evolve(state: State, e: Event): State =
    when(e) {
        is WarenkorbWurdeAngelegt-> warenkorbWurdeAngelegt(state, e.warenkorb)
        is ProduktWurdeInWarenkorbGelegt -> produktWurdeInWarenkorbGelegt(state, e.warenkorb, e.produkt)
        is WarenkorbWurdeBestellt -> warenkorbWurdeBestellt(state, e.warenkorb)
        else -> throw IllegalArgumentException("Unkown event $e")
    }

fun produktWurdeInWarenkorbGelegt(state: State, warenkorb: WarenkorbId, produkt: ProduktId): State {
    return state.copy(produkte = state.produkte + produkt)
}

fun warenkorbWurdeAngelegt(state: State, warenkorb: WarenkorbId): State {
    return State(warenkorb)
}

fun warenkorbWurdeBestellt(state: State, warenkorb: WarenkorbId): State {
    return state.copy(warenkorbOffen = false)
}
