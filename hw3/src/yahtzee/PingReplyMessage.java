package yahtzee;

public class PingReplyMessage extends AbstractMessage{

    private boolean isLeft;
    private int numToLeft;
    private int numInSeries;
    private Status state;
    private int total;

    public PingReplyMessage(int correlationId, Component sender,boolean isLeft,int numToLeft,int numInSeries,Status state,int total){
        super(correlationId,sender);
        this.isLeft = isLeft;
        this.numToLeft = numToLeft;
        this.numInSeries = numInSeries;
        this.state = state;
        this.total = total;
    }
    @Override
    public void dispatch(Component receiver) {
        receiver.handlePingReply(this);
    }

    public boolean isLeft() {
        return isLeft;
    }

    public int getNumToLeft() {
        return numToLeft;
    }

    public int getNumInSeries() {
        return numInSeries;
    }

    public Status getState() {
        return state;
    }

    public int getTotal() {
        return total;
    }
}
