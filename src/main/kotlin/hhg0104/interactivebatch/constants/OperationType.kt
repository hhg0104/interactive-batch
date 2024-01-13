package hhg0104.interactivebatch.constants

enum class OperationType(val id: Int, val operationName: String) {

    CARSHARE_ORIX_DATA_UPDATE(1, "CarShare - Orix Data Update Operation"),
    CARSHARE_DELETE_USER_INFO(2, "CarShare - Deletion Of User Registration Information Operation");

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