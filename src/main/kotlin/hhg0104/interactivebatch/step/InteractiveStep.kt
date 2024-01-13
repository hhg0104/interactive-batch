package hhg0104.interactivebatch.step

import org.beryx.textio.TextIO

interface InteractiveStep {
    fun proceed(textIO: TextIO, stepData: InteractiveStepData)
}