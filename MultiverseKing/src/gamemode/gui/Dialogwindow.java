package gamemode.gui;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector2f;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class Dialogwindow extends EditorWindow {

    protected final DialogWindowListener listener;
    private Window popup;
    private boolean enabled = true;

    public Dialogwindow(Screen screen, String WindowName, DialogWindowListener listener) {
        super(screen, null, WindowName);
        this.listener = listener;
    }

    public final void show() {
        addButtonList("staticField", new String[]{"Confirm", "Cancel"}, HAlign.right);
        screen.getApplication().getInputManager().addListener(dialogPopupListener, new String[]{"confirmDialog", "cancelDialog"});
        super.show(null, null);
    }

    public void addInputText(String labelName) {
        addTextField("Name", null, HAlign.left);
    }

    public void addButton(String labelName) {
        addButtonField(labelName, HAlign.full);
    }

    public void addSpinnerField(String labelName) {
        addSpinnerField(labelName, new int[]{1, 10, 1, 2}, HAlign.left);
    }

    @Override
    protected void onButtonTrigger(String labelName) {
        if (labelName.equals("Confirm")) {
            listener.onDialogTrigger(name, true);
        } else if (labelName.equals("Cancel")) {
            listener.onDialogTrigger(name, false);
        }
    }

    @Override
    public void removeFromScreen() {
        removeMapping();
        super.removeFromScreen();
    }

    private void removeMapping() {
        screen.getApplication().getInputManager().removeListener(dialogPopupListener);
    }
    private final ActionListener dialogPopupListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (enabled) {
                if (name.equals("confirmDialog") && !isPressed) {
                    onButtonTrigger("Confirm");
                } else if (name.equals("cancelDialog") && !isPressed) {
                    onButtonTrigger("Cancel");
                }
            }
        }
    };

    @Override
    protected void onTextFieldInput(String UID, String input, boolean triggerOn) {
        if (triggerOn) {
            onButtonTrigger("Confirm");
        } else {
            onButtonTrigger("Cancel");
        }
    }

    @Override
    public boolean isVisible() {
        if (getWindow() != null) {
            return getWindow().getIsVisible();
        } else {
            return false;
        }
    }

    public String getTextInput(String name) {
        return getTextField(name).getText();
    }

    public int getInput(String name) {
        return getSpinnerField(name).getSelectedIndex();
    }

    @Override
    public void hide() {
        if(window != null && popup != null){
            popup.removeFromParent();
        }
        super.hide();
    }

    
    
    public void popupBox(String message) {
        if (popup == null) {
            popup = new Window(screen, getUID() + "popupBox", new Vector2f(0, getWindow().getHeight()), new Vector2f(getWindow().getWidth(), 25));
            popup.removeAllChildren();
            getWindow().addChild(popup);
        } else if (!popup.getIsVisible()) {
            getWindow().addChild(popup);
        }
        popup.setText(message);
    }

    public void enable(boolean enabled) {
        this.enabled = enabled;
    }
}
