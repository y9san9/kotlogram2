package examples.auth

import examples.client
import java.util.*


val scanner = Scanner(System.`in`)
fun main(){
    print("Enter phone: ")
    val phone = scanner.nextLine()
    client.auth(phone) {
        code {
            do {
                print("Enter code from telegram: ")
                val code = scanner.nextLine()
            } while (!check(code))
        }
        password {
            do {
                print("Enter account password: ")
                val password = scanner.nextLine()
            } while (!check(password))
        }
        signed {
            println("Signed in as ${it.firstName}")
        }
    }
}
