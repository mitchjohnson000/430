public class CountDownLatch {

    private int count;
    private final Object synch = new Object();

    public CountDownLatch(int numOfThreads) {
        this.count = numOfThreads;
    }

    public synchronized void await() throws InterruptedException {
        while(count > 0){
            synch.wait();
        }
    }

    public synchronized void countDown(){
        if(--count <= 0){
            synch.notifyAll();
        }
    }
}