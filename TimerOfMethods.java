public class TimerOfMethods {
    private static long startTime = 0;

    static void startTimer(){
        startTime = System.nanoTime();
    }

    static long getEllapsedTime(){
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    static void printEllapsedTime(){
        //long l = ; // Do this first since printing takes time - 1000000 is ns to ms
        System.out.println("Elapsed time: " + getEllapsedTime()/1000000);
    }
}
