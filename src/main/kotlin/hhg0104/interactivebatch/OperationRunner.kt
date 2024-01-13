package hhg0104.interactivebatch

import hhg0104.interactivebatch.step.InteractiveStepBuilder
import hhg0104.interactivebatch.step.OperationTypeInputStep
import hhg0104.interactivebatch.step.RTRVTicketCheckStep


class OperationRunner {

    fun run() {
        InteractiveStepBuilder
            .init(addNewLineBetweenSteps = true)
            .next(OperationTypeInputStep())
            .next(RTRVTicketCheckStep())
            .proceed()
    }
}