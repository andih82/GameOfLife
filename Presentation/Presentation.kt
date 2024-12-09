import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/*
suspend fun brake(){
    println("taking a break")
    yield()
}

fun main(){
    runBlocking {
        launch{
            println("A: Setting up the hose!")
            brake()
            println("A: Spraying water on the fire!")
            brake()
            println("A: Securing the scene!")
        }
        launch{
            println("B: Assessing the fire!")
            brake()
            println("B: Rescuing trapped individuals!")
            brake()
            println("B: Giving the all-clear!")
        }
    }
}
*/

enum class Product(val description: String) {
    WHEELS("wheels"),
    WINDOWS("windows")
}

suspend fun order(product: Product): Product {
    println("${product.description} on the way")
    delay(1000)
    return product
}

suspend fun perform(task: String) {
    println("Performing: $task")
    delay(500)
}

fun main() {
    val totalTime = measureTimeMillis {
        runBlocking {
            val wheels = async(Dispatchers.IO) { order(Product.WHEELS) }
            val windows = async(Dispatchers.IO) {
                throw Exception("Windows out of stock!")
                order(Product.WINDOWS)
            }

            launch(Dispatchers.Default) {
                perform("Building car body")
                launch { perform("attaching ${wheels.await().description}") }
                launch { perform("attaching ${windows.await().description}") }
            }
        }
    }
    println("Time taken: ${totalTime}ms")
}

