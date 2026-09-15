package UI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BUTTON_Button extends SuperButton {

	private boolean mouseDown = false;

	public BUTTON_Button(int width, int height) {
		super(width, height);
	}

	@Override
	public void setupButtonBehavior() {
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (!mouseDown)
					toggled = true;
				mouseDown = true;
				updateColor();

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				toggled = false;
				mouseDown = false;
				run();
				updateColor();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});
	}

}
