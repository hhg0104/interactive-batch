package hhg0104.interactivebatch.batch.data_update_batch.data

import java.io.File

class SQLFiles(sqlFile: File, diffFile: File) {

    var sqlFile: File? = null

    var diffFile: File? = null

    init {
        this.sqlFile = sqlFile
        this.diffFile = diffFile
    }
}