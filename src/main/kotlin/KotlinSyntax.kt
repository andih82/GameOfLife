fun main(){


    println("Hello, World!")
    val result = add(2, 3)
    println("Result: $result")
    val result2 = add() // uses default values
    println("Result2: $result2")


    val result3 = intOperation(2, 3, ::add)
    println("Result3: $result3")
    val result4 = intOperation(3,  operation =  { a, b -> a * b }, b=2 )
    println("Result4: $result4")
    val result5 = intOperation(2, 3) { a, b -> a - b }
    println("Result5: $result5")


    val printResult = fun Int.(prefix:String,  other: Int, operation : ( Int , Int) -> Int) = println( "$prefix ${operation(this, other)}")
    2.printResult("Result6:", 3, ::add)
    2.printResult("Result7:",3, { a, b -> a * b })
    2.printResult("Result8:", 3) { a, b -> a - b }

}

fun add(a: Int = 1, b: Int = 2): Int {
    return a + b
}

fun intOperation(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

