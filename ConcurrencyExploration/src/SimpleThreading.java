/**
 * Created by saggezza on 7/22/14.
 */
public class SimpleThreading {
    static void threadMessage(String message){
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class MessageLoop implements Runnable{
        public void run(){
            String[] messages = {"Hello World", "Kevin Here", "How are you?"};
            try {
                for (int i=0; i<messages.length; i++){
                    Thread.sleep(4000);
                    threadMessage(messages[i]);
                }
            }
            catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException{
        //Set patience, if argument present, it is the patience
        long patience = 1000*60*60;
        if (args.length>0){
            try {
                patience = Long.parseLong(args[0]);
            }
            catch (NumberFormatException e){
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }

        threadMessage("Starting MessageLoop thread");
        long startTime = System.currentTimeMillis();
        Thread t = new Thread(new MessageLoop());
        t.start();

        threadMessage("Waiting for MessageLoop to finish.");
        while(t.isAlive()){
            //Thread message while waiting, wait 1 second for join
            threadMessage("Still waiting...");
            t.join(1000);
            if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()){
                threadMessage("Tired of waiting!");
                t.interrupt();
                t.join();
            }
        }
        threadMessage("Finally!");
    }
}
