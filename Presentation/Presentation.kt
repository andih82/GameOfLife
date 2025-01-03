import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

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

data class Product(val name: String, val price: Double)

suspend fun priceFromAmazon(product: String): Product {
    delay(1000)
    println("Price from Amazon: 599.99")
    return Product(product, 599.99)
}
suspend fun priceFromMediaMarkt(product: String): Product {
    delay(800)
    println("Price from MediaMarkt: 549.99")
    return Product(product, 549.99)
}
suspend fun orderProduct(product: Product) {
    println("Ordering ${product.name} for ${product.price}!")
}

suspend fun sendOrderConfirmation(product: Product) {
    println("Sending order confirmation for ${product.name} priced at ${product.price}.")
}

fun main(){
    runBlocking {
        val product = "TV"
        val amazonPrice = async(Dispatchers.IO) { priceFromAmazon(product) }
        val mediaMarktPrice = async(Dispatchers.IO) {
            throw Exception("Media Markt is unavailable!")
            priceFromMediaMarkt(product)
            }

        launch(Dispatchers.Main) {
            val priceFromAmazon = amazonPrice.await()
            val priceFromMediaMarkt = mediaMarktPrice.await()
            val cheaperProduct = if (priceFromAmazon.price < priceFromMediaMarkt.price) {
                priceFromAmazon
            } else {
                priceFromMediaMarkt
            }
            println("Cheaper product: ${cheaperProduct.name} at ${cheaperProduct.price}")
            launch { orderProduct(cheaperProduct) }
            launch { sendOrderConfirmation(cheaperProduct) }
            println("Completed the price comparison and ordering process!")
        }
    }
}

/*
fun main() {
    runBlocking {
        //The coroutine started by launch is a child
        //of the parent runBlocking coroutine.
        launch {
            delay(1.seconds)
            launch {
                delay(250.milliseconds)
                println("Grandchild done")
            }
            println("Child 1 done!")
        }
        launch {
            delay(500.milliseconds)
            println("Child 2 done!")
        }
        println("Parent done!")
    }
}

 */
/*
fun String.greet() = "Hello, $this!"

fun main() {
    val name = "World"
    println(name.greet())

    val numbers = listOf(1, 2, 3)
    val doubled = numbers.map { it * 2 }
    println(doubled)
}
*/

/*
suspend fun calculateSomething(): Int {
    delay(3.seconds)
    return 2 + 2
}

fun main() = runBlocking {
    val quickResult = withTimeoutOrNull(500.milliseconds) {
        calculateSomething()
    }
    println(quickResult)

    val slowResult = withTimeoutOrNull(5.seconds) {
        calculateSomething()
    }
    println(slowResult)
}
 */


/*
fun myHelloFunction(name : String = "World", action : String.() -> Unit = { println(this)}){
    val greeting = StringBuilder().apply {
        this.append("Hello, ")  // <this> is the StringBuilder
        append(name)            // <this> is omitted
    }.toString()
    greeting.action()
}

fun main() {
    myHelloFunction("Andi") { println(this) }
    myHelloFunction("Tim") { println(this) }
    myHelloFunction { println(this.uppercase())}
    myHelloFunction()
}
*/

