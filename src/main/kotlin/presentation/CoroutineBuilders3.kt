import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main() {
    comparePrices3()
}

fun comparePrices3(){
    runBlocking(Dispatchers.Default) {
        println("runBlocking started!")
        val amazonPrice = async(Dispatchers.IO) {
            delay(500.milliseconds)
            getAmazonPrice("TV")
        }
        val mediaMarktPrice = async(Dispatchers.IO) {
            delay(1000.milliseconds)
            getMediaMarktPrice("TV")
        }
        launch {
            println("MediaMarkt price: ${mediaMarktPrice.await()}")
        }
        launch {
            println("Amazon price: ${amazonPrice.await()}")
        }
        println("runBlocking pending ...")
    }
    println("runBlocking finished!")
}
