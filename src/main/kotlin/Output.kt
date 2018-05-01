import java.util.concurrent.BlockingQueue

/**
 * A "smart" consumer that will output messages from the queue and handle interruptions and shutdowns gracefully
 */
class Output(private val queue: BlockingQueue<String>) : Runnable {

    override fun run() {

        // Instead of an endless loop we just loop until someone signals us to stop
        while (!Thread.currentThread().isInterrupted) {

            try {
                output(queue.take())
            } catch (e: InterruptedException) {
                // If we're interrupted we want to work off all items that are currently in the queue
                // Future messages will not be handled
                clearQueue()

                /*
                 Catching the InterruptedExceptions makes the thread no longer interrupted (since it assumes we
                 handled it in the catch block). If we want the thread to stay interrupted (for whatever reason) we
                 have to interrupt ourselves. Since our while loop is checking for the interrupted status we need to
                 interrupt to end the loop
                  */
                Thread.currentThread().interrupt()
            }
        }

        // We are done, thread can die now †
        println("----- Output done -----")
    }

    /**
     * Handles all messages that are currently in the queue
     */
    private fun clearQueue() {
        val remainingMessages = mutableListOf<String>()
        queue.drainTo(remainingMessages)
        println("----- output queue drained - new messages will be ignored -----")
        println("----- remaining messages: ${remainingMessages.joinToString()}")
        remainingMessages.forEach(this::output)
    }

    /**
     * Simulates a lengthy output operation like writing to a file
     */
    private fun output(message: String) {
        Thread.sleep(500)
        println("Received Message: \"$message\"")
    }

}
