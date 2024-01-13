package hhg0104.interactivebatch.step

import hhg0104.interactivebatch.batch.delete_user_data_batch.step.DeleteSQLGenerationStep
import hhg0104.interactivebatch.constants.OperationType
import hhg0104.interactivebatch.batch.data_update_batch.step.ExcelReadStep
import hhg0104.interactivebatch.batch.data_update_batch.step.SQLGenerationStep
import org.beryx.textio.TextIO
import org.beryx.textio.TextIoFactory

class InteractiveStepBuilder {

    val interactiveSteps = ArrayList<InteractiveStep>()

    val stepData = InteractiveStepData()

    var addNewLineBetweenSteps = false

    var textIO: TextIO? = null

    private constructor()

    companion object {
        fun init(addNewLineBetweenSteps: Boolean = false): InteractiveStepBuilder {
            val builder = InteractiveStepBuilder()
            builder.textIO = TextIoFactory.getTextIO()
            builder.addNewLineBetweenSteps = true
            return builder
        }
    }

    fun next(step: InteractiveStep): InteractiveStepBuilder {
        this.interactiveSteps.add(step)
        return this
    }

    fun nextAll(steps: List<InteractiveStep>): InteractiveStepBuilder {
        steps.forEach { next(it) }
        return this
    }

    fun proceed(): InteractiveStepBuilder {
        if (this.textIO == null) {
            println(ColorLog.logError("Need to call the 'init' method first."))
            return this
        }

        NewLineStep().proceed(this.textIO!!, this.stepData)

        interactiveSteps.forEach {
            it.proceed(this.textIO!!, this.stepData)
            if (this.addNewLineBetweenSteps) {
                NewLineStep().proceed(this.textIO!!, this.stepData)
            }
        }

        getAdditionalSteps().forEach {
            it.proceed(this.textIO!!, this.stepData)
            if (this.addNewLineBetweenSteps) {
                NewLineStep().proceed(this.textIO!!, this.stepData)
            }
        }

        return this
    }

    private fun getAdditionalSteps(): MutableList<InteractiveStep> {

        when (this.stepData.operationType) {
            OperationType.CARSHARE_ORIX_DATA_UPDATE -> {
                return mutableListOf(ExcelReadStep(), SQLGenerationStep())
            }

            OperationType.CARSHARE_DELETE_USER_INFO -> {
                return mutableListOf(DeleteSQLGenerationStep())
            }
            else -> {}
        }

        return mutableListOf()
    }
}