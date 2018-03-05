package yahtzee;

public class PingMessage extends AbstractMessage {

    private boolean isLeft;

    public PingMessage(Component sender, boolean isLeft) {
        super(sender);
        this.isLeft = isLeft;
    }

    @Override
    public void dispatch(Component receiver) {
        receiver.handlePing(this);
    }

    public boolean isLeft() {
        return isLeft;
    }
}
