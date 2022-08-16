package sean.k.common.date

//Allows injection of mocked Clocks to test time logic
interface Clock {
    val currentTimeMillis: Long
}

class DefaultClock: Clock {
    override val currentTimeMillis: Long
        get() = System.currentTimeMillis()
}