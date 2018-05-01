import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * A sample app that starts a input thread which will generate messages. These messages will be processed in some way
 * and then sent to a output thread.
 * The messages are sent between threads via blocking queues
 * The main focus is on how to shut the app down without losing important messages
 */
fun main(args: Array<String>) {
    val inputQueue: BlockingQueue<String> = LinkedBlockingQueue()
    val outputQueue: BlockingQueue<String> = LinkedBlockingQueue()
    val inputThread = Thread(Input(inputQueue))
    val outputThread = Thread(Output(outputQueue))

    // Settings the isDaemon flag tells the JVM to exit, even if this thread hasn't finished yet
    // This is ok for the input thread - when the other threads are done, we don't need the input
    inputThread.isDaemon = true
    inputThread.start()

    // The JVM should stay alive until the output thread is done - so we don't set the isDeamon flag
    outputThread.start()

    var message: String

    // Take the message, do something with it and send it to the output. Do this until some condition is met and we
    // want to stop
    do {
        message = inputQueue.take()
        outputQueue.put(transform(message))
    } while (!exitCondition(message))

    // We are done but the output thread is still alive and well - we need to tell it to stop. This will not immediately
    // end the thread but throw a InterruptException (which is handled in the Output class)
    outputThread.interrupt()

    /*
     The input thread will still continue generating messages (as long as either the main or output thread is alive) but
     nobody will take the messages from the input queue. So it could make sense to also interrupt the input thread here.
     But we don't do that to show that the program will exit even though the input thread is still running (since it's
     a daemon thread)
     */

    /*
     Adding something to the queue after the outputThread has been interrupted is not guaranteed to be printed.
     This depends on whether this line of code or the interrupt code in the Ouput is executed first - this is a race
     condition
     */
    outputQueue.put("Might be printed")

    Thread.sleep(1)
    /*
     After sleeping for 1ms the chances are extremely high that the output thread has received the InterruptException
     and started the clearQueue function and therefore will not react to new messages from the queue
     */
    outputQueue.put("Probably won't be printed ")

    println("----- Main done -----")

}

/**
 * Process the messages
 */
fun transform(message: String): String {
    return "Transformed $message"
}

/**
 * End when the message "5" has been sent
 */
private fun exitCondition(message: String) = message == "5"

