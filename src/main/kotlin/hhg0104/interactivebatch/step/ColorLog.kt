package hhg0104.interactivebatch.step

import hhg0104.interactivebatch.util.ConsoleColor

class ColorLog {

    companion object {
        fun log(message: String, color: ConsoleColor): String {
            return ConsoleColor.addColor(message, color)
        }

        fun logNormal(message: String): String {
            return "[-] $message"
        }

        fun logInput(message: String): String {
            return ConsoleColor.addColor("> $message", ConsoleColor.YELLOW)
        }

        fun logError(message: String): String {
            return ConsoleColor.addColor("[!] $message", ConsoleColor.RED)
        }

        fun logWarning(message: String): String {
            return ConsoleColor.addColor("[!] $message", ConsoleColor.PURPLE)
        }

        fun logResult(message: String): String {
            return ConsoleColor.addColor(message, ConsoleColor.GREEN)
        }
    }
}