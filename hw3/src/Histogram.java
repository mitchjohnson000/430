import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Creates a histogram of values produced by Java's random
 * number generator.
 */
public class Histogram
{  
  public static void main(String[] args)
  {
    try{
      new Histogram(10000, 100000000).go();
    }catch (ConcurrentModificationException e){

    }catch (ExecutionException e){

    }catch (InterruptedException e){

    }

  }
  
  private int maxValue;
  private int numSamples;
  private int[] results;
  
  public Histogram(int givenMax, int givenNum)
  {
    maxValue = givenMax;
    numSamples = givenNum;
    results = new int[maxValue];
  }
  
  public void go() throws ConcurrentModificationException, InterruptedException, ExecutionException
  {
    FutureWorker futureWorker = new FutureWorker();

    long start = System.currentTimeMillis();

    Future<int[]> future1 = futureWorker.calculate();
    Future<int[]> future2 = futureWorker.calculate();
    Future<int[]> future3 = futureWorker.calculate();
    Future<int[]> future4 = futureWorker.calculate();


    while (!(future1.isDone() && future2.isDone())) {

    }

    int[] partition1 = future1.get();
    int[] partition2 = future2.get();
    int[] partition3 = future3.get();
    int[] partition4 = future4.get();

    for(int i = 0; i < results.length; i++){
      results[i] = partition1[i] + partition2[i] + partition3[i] + partition4[i];
    }
    long elapsed = System.currentTimeMillis() - start;

    int total = 0;
    for (int i = 0; i < results.length; ++i)
    {
      total += results[i];
      System.out.println(i + ": " + results[i]);
    }
    System.out.println();
    System.out.println("Check total samples: " + total);
    System.out.println("Elapsed: " + elapsed);

  }

  class FutureWorker {

    private ExecutorService executor = Executors.newFixedThreadPool(4);

    public Future<int[]> calculate(){
      return executor.submit(() -> {
        int []localResults = new int[maxValue];
        Random rand = new Random();
        for (int i = 0; i < (numSamples / 4); ++i) {
          int next = rand.nextInt(maxValue);
          localResults[next]++;
        }
        return localResults;
      });


    }


  }


}
