package hhg0104.interactivebatch.constants

enum class OperationType(val id: Int, val operationName: String) {

    DATA_UPDATE(1, "Data Update Operation"),
    DELETE_USER_INFO(2, "Deletion Of User Registration Information Operation");

    companion object {
        fun getTypeById(id: Int): OperationType? {
            for (type in OperationType.values()) {
                if (type.id == id){
                    return type;
                }
            }
            return null;
        }
    }
}