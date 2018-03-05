package yahtzee;

public class PingReplyMessage extends AbstractMessage{

    private boolean isLeft;
    private int numToLeft;

    public PingReplyMessage(Component sender,boolean isLeft,int numToLeft){
        super(sender);
        this.isLeft = isLeft;
        this.numToLeft = numToLeft;
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
}
