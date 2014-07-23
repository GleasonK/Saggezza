/**
 * Created by saggezza on 7/22/14.
 */
public class MessagePause {
    public static void main(String[] args) {
        String[] messages = {"Hello World", "Kevin Here", "How are you?"};
        for (int i=0; i<messages.length; i++){
            try {
                Thread.sleep(4000);
            }
            //Thrown by sleep, stops for interrupt
            catch (InterruptedException e) {
                return;
            }
            //If thread does not throw interrupt exception, constantly check for interrupts.
            if (Thread.interrupted()){
                return;
                //Alternatively throw new InterruptedException();
            }
            System.out.println(messages[i]);
        }
    }
}
