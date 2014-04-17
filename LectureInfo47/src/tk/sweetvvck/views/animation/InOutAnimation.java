package tk.sweetvvck.views.animation;

import android.view.View;
import android.view.animation.AnimationSet;

public abstract class InOutAnimation extends AnimationSet
{

    public static final class Direction 
    {
    	public static final int IN = 0;
    	public static final int OUT = 1;
    }


    public InOutAnimation(int dir, long duration, View aview[])
    {
    	super(true);
        direction = dir;
        if(direction == Direction.IN){//in
        	addInAnimation(aview);
        } else if(direction == Direction.OUT){//out
        	addOutAnimation(aview);
        }
        setDuration(duration);
    }

    protected abstract void addInAnimation(View aview[]);

    protected abstract void addOutAnimation(View aview[]);

    public final int direction;
}
