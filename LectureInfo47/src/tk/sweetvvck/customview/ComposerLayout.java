package tk.sweetvvck.customview;

import tk.sweetvvck.views.animation.ComposerSlideAnimation;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.RelativeLayout;


public class ComposerLayout extends RelativeLayout {

	public ComposerLayout(Context context) {
		super(context);
		originalPaddingLeft = getPaddingLeft();
		originalPaddingTop = getPaddingTop();
		originalPaddingRight = getPaddingRight();
		originalPaddingBottom = getPaddingBottom();
	}

	public ComposerLayout(Context context, AttributeSet attributeset) {
		super(context, attributeset);
		originalPaddingLeft = getPaddingLeft();
		originalPaddingTop = getPaddingTop();
		originalPaddingRight = getPaddingRight();
		originalPaddingBottom = getPaddingBottom();
	}

	public void moveDown(int i) {
		int i1 = originalPaddingBottom - i;
		setPadding(originalPaddingLeft, originalPaddingTop,
				originalPaddingRight, i1);
	}

	protected void onAnimationEnd() {
		super.onAnimationEnd();
		if (animation instanceof ComposerSlideAnimation) {
			int yOffSet = ((ComposerSlideAnimation) animation).yOffset;
			moveDown(yOffSet);
		}
	}

	public void resetPosition() {
		setPadding(originalPaddingLeft, originalPaddingTop,
				originalPaddingRight, originalPaddingBottom);
	}

	public void startAnimation(Animation animation1) {
		super.startAnimation(animation1);
		animation = animation1;
	}

	private Animation animation;
	private final int originalPaddingBottom;
	private final int originalPaddingLeft;
	private final int originalPaddingRight;
	private final int originalPaddingTop;
}
