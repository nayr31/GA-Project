public class TimerOfMethods {
    private static long startTime = 0;

    static void startTimer(){
        startTime = System.nanoTime();
    }

    static long getEllapsedTime(){
        long endTime = System.nanoTime();
        return endTime - startTime;
    }
}
