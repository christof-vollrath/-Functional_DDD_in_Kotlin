package net.taobits.functionaldddkotlin

import io.kotlintest.specs.StringSpec

class WarenkorbTest : StringSpec() { init {
    "Angelegter Warenkorb ist angelegt" {
        emptyList<Event>()
        .apply(LegeWarenkorbAn(warenkorb = WarenkorbId(1), kunde = KundeId("Marco"))) shouldBe
            listOf(WarenkorbWurdeAngelegt(warenkorb = WarenkorbId(1), kunde = KundeId("Marco")))
    }

    "In einen nicht angelegten Warenkorb kann kein Produkt gelegt werden" {
        val exception = shouldThrow<HandleCommandException> {
            emptyList<Event>()
            .apply(ProduktInWarenkorbLegen(warenkorb = WarenkorbId(1), produkt = ProduktId(1)))
        }
        exception.message shouldBe "Warenkorb muss angelegt sein"
    }

    "Produkt in Warenkorb legen" {
        listOf(WarenkorbWurdeAngelegt(warenkorb = WarenkorbId(1), kunde = KundeId("Marco")))
        .apply(ProduktInWarenkorbLegen(warenkorb = WarenkorbId(1), produkt = ProduktId(1))) shouldBe
            listOf(ProduktWurdeInWarenkorbGelegt(warenkorb = WarenkorbId(1), produkt = ProduktId(1)))
    }

    "Ein nicht angelegter Warenkorb kann nicht bestellt werden" {
        val exception = shouldThrow<HandleCommandException> {
            emptyList<Event>().apply(BestelleWarenkorb(warenkorb = WarenkorbId(1)))
        }
        exception.message shouldBe "Warenkorb muss angelegt sein"
    }

    "Ein leerer Warenkorb kann nicht bestellt werden" {
        val exception = shouldThrow<HandleCommandException> {
            listOf(WarenkorbWurdeAngelegt(warenkorb = WarenkorbId(1), kunde = KundeId("Marco")))
            .apply(BestelleWarenkorb(warenkorb = WarenkorbId(1)))
        }
        exception.message shouldBe "Warenkorb darf nicht leer sein"
    }

    "Produkt wurde in falschen Warenkorb gelegt und kann nicht bestellt werden" {
        val exception = shouldThrow<HandleCommandException> {
            listOf(WarenkorbWurdeAngelegt(warenkorb = WarenkorbId(1), kunde = KundeId("Marco")),
                   ProduktWurdeInWarenkorbGelegt(warenkorb = WarenkorbId(2), produkt = ProduktId(1)))
            .apply(BestelleWarenkorb(warenkorb = WarenkorbId(1)))
        }
        exception.message shouldBe "Warenkorb darf nicht leer sein"
    }

    "Ein Warenkorb kann bestellt werden" {
        listOf(WarenkorbWurdeAngelegt(warenkorb = WarenkorbId(1), kunde = KundeId("Marco")),
               ProduktWurdeInWarenkorbGelegt(warenkorb = WarenkorbId(1), produkt = ProduktId(1)))
        .apply(BestelleWarenkorb(warenkorb = WarenkorbId(1))) shouldBe
            listOf(WarenkorbWurdeBestellt(warenkorb = WarenkorbId(1)))
    }

    "Nach einer Bestellung kann nichts mehr in den Warenkorb gelegt werden" {
        val exception = shouldThrow<HandleCommandException> {
            listOf(WarenkorbWurdeAngelegt(warenkorb = WarenkorbId(1), kunde = KundeId("Marco")),
                   ProduktWurdeInWarenkorbGelegt(warenkorb = WarenkorbId(1), produkt = ProduktId(1)),
                   WarenkorbWurdeBestellt(warenkorb = WarenkorbId(1)))
            .apply(ProduktInWarenkorbLegen(warenkorb = WarenkorbId(1), produkt = ProduktId(1)))
        }
        exception.message shouldBe "Warenkorb schon bestellt"
    }
}}


fun List<Event>.apply(command: Command): List<Event> {
    val initState = this.fold(State()) { t, n ->
        if (n is WarenkorbEvent && command is WarenkorbCommand &&
                n.warenkorb == command.warenkorb)
            evolve(t, n)
        else t
    }
    println()
    println("Given events:")
    this.forEach { println(it) }
    println("When command:")
    println(command)
    try {
        val result = handle( command, initState)
        println("Result:")
        println(result)
        return result
    } catch (e: HandleCommandException) {
        println(e.message)
        throw e
    }
}
