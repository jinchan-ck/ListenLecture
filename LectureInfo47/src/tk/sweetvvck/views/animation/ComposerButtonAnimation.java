package tk.sweetvvck.views.animation;

import tk.sweetvvck.customview.InOutImageButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;


public class ComposerButtonAnimation extends InOutAnimation
{

    public ComposerButtonAnimation(int direction, int duration, View view)
    {
        super(direction, duration, new View[]{view});
    }

    public static void startAnimations(ViewGroup viewgroup, int direction)
    {
    	if(direction == Direction.IN){//in
    		startAnimationsIn(viewgroup);
        } else if(direction == Direction.OUT){//out
        	startAnimationsOut(viewgroup);
        }
    }

    private static void startAnimationsIn(ViewGroup viewgroup)
    {
        int count = viewgroup.getChildCount();
        for(int i=0; i<count; i++){
        	if(viewgroup.getChildAt(i) instanceof InOutImageButton)
            {
                InOutImageButton imgView = (InOutImageButton)viewgroup.getChildAt(i);
                ComposerButtonAnimation animation = new ComposerButtonAnimation(InOutAnimation.Direction.IN, 200, imgView);
                long startOffset = i * 100 / (count -1);
                animation.setStartOffset(startOffset);
                animation.setInterpolator(new OvershootInterpolator(2F));
                imgView.startAnimation(animation);
            }
        }
    }

    private static void startAnimationsOut(ViewGroup viewgroup)
    {
    	
        int count = viewgroup.getChildCount();
        for(int i=0; i<count; i++){
        	if(viewgroup.getChildAt(i) instanceof InOutImageButton)
            {
                InOutImageButton inoutimagebutton = (InOutImageButton)viewgroup.getChildAt(i);
                int direction = InOutAnimation.Direction.OUT;
                ComposerButtonAnimation anim = new ComposerButtonAnimation(direction, 200, inoutimagebutton);
                long startOffset = (count -1 - i) * 100 / (count -1);
                anim.setStartOffset(startOffset);
                anim.setInterpolator(new AnticipateInterpolator(2F));
                inoutimagebutton.startAnimation(anim);
            }
        }
    	
    }

    protected void addInAnimation(View aview[])
    {
        ViewGroup.MarginLayoutParams marginlayoutparams = (ViewGroup.MarginLayoutParams)aview[0].getLayoutParams();
        float fromX = -marginlayoutparams.leftMargin + xOffset;
        float fromY = marginlayoutparams.bottomMargin - 13;
        TranslateAnimation anim = new TranslateAnimation(fromX, 0F, fromY, 0F);
        addAnimation(anim);
    }

    protected void addOutAnimation(View aview[])
    {
        android.view.ViewGroup.MarginLayoutParams marginlayoutparams = (android.view.ViewGroup.MarginLayoutParams)aview[0].getLayoutParams();
        float toX = -marginlayoutparams.leftMargin + xOffset;
        float toY = marginlayoutparams.bottomMargin - 13;
        TranslateAnimation anim = new TranslateAnimation(0F, toX, 0F, toY);
        addAnimation(anim);
    }

    private static final int xOffset = 16;
    //private static final int yOffset = 243;
}
