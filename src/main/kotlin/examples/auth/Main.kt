package examples.auth

import examples.client
import java.util.*


val scanner = Scanner(System.`in`)
fun main(){
    print("Enter phone: ")
    scanner.nextLine().also { phone ->
        client.auth(phone, {
            print("Enter password: ")
            scanner.nextLine()
        }){
            print("Enter code: ")
            scanner.nextLine()
        }
    }
}
