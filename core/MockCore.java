package core;

/**
 * @author Mohammad Oloomi
 */
public class MockCore implements ICoreProxy {

    private double failureRate;
    private ExpRndGen rnd;
    private int refId;

    public MockCore(double failureRate, double responseTimeMean) {
        this.failureRate = failureRate;
        rnd = new ExpRndGen(responseTimeMean);
        refId = 0;
    }

    public boolean purchaseRequest() throws CoreTimeoutException {
        if (Math.random() < failureRate) {
            throw new CoreTimeoutException();
        }
        try {
            Thread.sleep((int) (1000 * rnd.next()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public int getRefId() {
        refId++;
        return refId;
    }

    public class ExpRndGen {

        private double mean;

        public ExpRndGen(double mean) {
            this.mean = mean;
        }

        public double next() {
            return -mean * Math.log(Math.random());
        }
    }
}
