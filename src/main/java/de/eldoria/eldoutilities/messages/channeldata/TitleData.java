package de.eldoria.eldoutilities.messages.channeldata;

public class TitleData implements ChannelData {
    public static final TitleData DEFAULT = new TitleData(10, 50, 20);
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleData(int fadeIn, int stay, int fadeOut) {
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }
}
