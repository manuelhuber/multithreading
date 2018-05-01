import java.util.concurrent.BlockingQueue

/**
 * A endless runnable that puts a message in the queue every 250ms
 */
class Input(private val queue: BlockingQueue<String>) : Runnable {

    private var counter = 0

    override fun run() {
        while (true) {
            queue.put(generateMessage())
            Thread.sleep(250)
        }
        // This line will never be printed - the while loop is endless and a interruption of this thread will make
        // it end abruptly
        println("Input done")
    }

    private fun generateMessage(): String {
        val message = "${counter++}"
        println("Generated message: \"$message\"")
        return message
    }

}
