package hhg0104.interactivebatch.util.db

import java.sql.Connection
import java.sql.DriverManager

class MySQLConnectUtil {

    companion object {
        fun createConnection(url: String, user: String, password: String): Connection? {
            Class.forName("com.mysql.cj.jdbc.Driver")
            return DriverManager.getConnection("jdbc:mysql://$url", user, password)
        }
    }
}