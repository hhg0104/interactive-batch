package hhg0104.interactivebatch

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TravelOperationToolApplication: CommandLineRunner {
    override fun run(vararg args: String?) {
        hhg0104.interactivebatch.OperationRunner().run()
    }
}

fun main(args: Array<String>) {
    runApplication<hhg0104.interactivebatch.TravelOperationToolApplication>(*args)
}