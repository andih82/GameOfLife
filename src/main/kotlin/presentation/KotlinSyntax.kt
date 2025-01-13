import java.lang.StringBuilder


fun main() {


    fun myHelloFunction(s: String) : String {
        val result = "Hello $s"
        return result
    }
    println( myHelloFunction("World") )


    fun String.myHelloExtensionFunction() : String {
        return "Hello $this"
    }
    println("World".myHelloExtensionFunction())

    fun String.myLengthExtFn() : String {
        val len = this.length
        return "Length $len"
    }
    println("World".myLengthExtFn())

    val lambdaFn =
        { s: String -> "Hello $s" }
    println(lambdaFn("World"))

    val stringList = listOf<String>("World", "Kotlin")
    stringList
        .map( { s-> "Hello $s" } )
        .forEach( { s-> println(s) } )

    stringList
        .map() { s -> "Hello $s" }
        .forEach { s -> println(s) }




    println(

        StringBuilder().run {
            this.append("Hello")
            append(" World")
            toString()
         }

    )

    fun myReceiverFn(s: String, fn: String.() -> String) : String {
        return s.fn()
    }

    println(

        myReceiverFn("World") {
            val len = length
            "Length $len"
        }
    )

    fun String.myReceiverExtFn(fn: String.() -> String) : String {
        return this.fn()
    }

    val string3 = "World".myReceiverExtFn {
        "Length $length"
    }
    println(string3)

    println(

        "World".myReceiverExtFn {
            "Hello $this"
        }

    )

    println(

    "World".myReceiverExtFn {
        val len = length
        "Length $len"
    }

    )

    println(

        "Hello Kotlin".myReceiverExtFn {
            uppercase()
        }

    )

    println(

    "Hello Kotlin".myReceiverExtFn {
        reversed()
    }

    )
}
