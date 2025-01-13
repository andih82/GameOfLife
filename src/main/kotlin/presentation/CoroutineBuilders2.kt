import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main() {
    comparePrices2()
}

fun comparePrices2(){
    runBlocking(Dispatchers.Default) {
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
    }
}
