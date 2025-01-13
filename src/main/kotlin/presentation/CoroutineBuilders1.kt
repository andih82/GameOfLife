import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main() {
    comparePrices1()
}

fun comparePrices1(){
    runBlocking {
        val amazonPrice = async {
            delay(500.milliseconds)
            getAmazonPrice("TV")
        }
        val mediaMarktPrice = async {
            delay(1000.milliseconds)
            getMediaMarktPrice("TV")
        }
        launch {
            println("MediaMarkt price: ${mediaMarktPrice.await()}")
        }
        launch {
            println("Amazon price: ${amazonPrice.await()}")
        }
    }
}

suspend fun getAmazonPrice(product: String): Double {
    return 599.99
}
suspend fun getMediaMarktPrice(product: String): Double {
    return 549.99
}