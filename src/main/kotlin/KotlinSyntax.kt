import Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import order
import perform
import kotlin.system.measureTimeMillis
import kotlin.time.Duration

fun main1(){
    myHelloFunction("Andi", { println(this) })
    myHelloFunction("Tim") { println(this) }
    myHelloFunction { println(this.uppercase())}
    myHelloFunction()


    runBlocking{
    }

    println("before runBlocking")
    runBlocking{
        launch {
            println("start launch")
            delay(500)
            println("World")
        }
        val result = async {
            println("start async")
            delay(1000)
            println("finished async")
            (17 + 4) * 2
        }
        println("print result: ${result.await()}")
        brake()
    }
    println("after runBlocking")
}

fun myHelloFunction(name : String = "World", action : String.() -> Unit = { println(this) }) {

    val greeting = StringBuilder().apply {
        this.append("Hello, ")  // <this> is the StringBuilder
        append(name)            // <this> is omitted
    }.toString()

    greeting.action()

}

fun main2(){
    println("before runBlocking")
    runBlocking{
        launch {
            println("start launch")
            delay(500)
            println("World")
        }
        val result = async {
            println("start async")
            delay(1000)
            println("finished async")
            (17 + 4) * 2
        }
        println("print result: ${result.await()}")
    }
    println("after runBlocking")
}

suspend fun brake(){
    println("taking a break")
    yield()
}

fun main() {
        runBlocking {
            val wheels = async { order(Product.WHEELS) }
            val windows = async { order(Product.WINDOWS) }
            launch {
                perform("Building car body")
                launch { perform("attaching ${wheels.await().description}") }
                launch { perform("attaching ${windows.await().description}") }
            }
        }
}

enum class Product(val description: String) {
    WHEELS("wheels"),
    WINDOWS("windows")
}

suspend fun order(product: Product, duration: Long = 1000): Product {
    println("${product.description} on the way")
    delay(duration)
    println("${product.description} arrived")
    return product
}

suspend fun perform(task: String) {
    println("Performing: $task")
    delay(500)
}
