package hhg0104.interactivebatch.step

import org.beryx.textio.TextIO

class NewLineStep : InteractiveStep {

    override fun proceed(textIO: TextIO, stepData: InteractiveStepData) {
        println("")
    }
}