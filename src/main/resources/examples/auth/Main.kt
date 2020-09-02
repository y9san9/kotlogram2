package examples.auth

import examples.client
import java.util.*


val scanner = Scanner(System.`in`)
fun main(){
    print("Enter phone: ")
    val phone = scanner.nextLine()
    client.auth(phone) {
        val code = code {
            if(!check(scanner.nextLine()))
                sentCode.cancel()
            else sentCode.resend()
        }
    }
}
