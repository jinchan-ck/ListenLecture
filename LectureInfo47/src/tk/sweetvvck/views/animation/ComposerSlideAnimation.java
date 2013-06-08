package tk.sweetvvck.views.animation;

import android.view.animation.TranslateAnimation;

public class ComposerSlideAnimation extends TranslateAnimation
{
    public final class Direction 
    {
        public static final int DOWN = 2;
        public static final int UP = 3;
    }


    public ComposerSlideAnimation(int yoffset, int direction)
    {
        super(0F, 0F, 0F, direction == Direction.UP? -yoffset : yoffset);
        yOffset = direction == Direction.UP? -yoffset:yoffset;
    }

    public final int yOffset;
}
