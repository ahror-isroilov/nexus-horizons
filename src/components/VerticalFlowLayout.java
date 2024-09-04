package components;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * author: ahror
 * <p>
 * since: 9/3/24
 */
public class VerticalFlowLayout extends FlowLayout {
    public enum Position {
        CENTER, CENTER_TOP, CENTER_BOTTOM, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
    private static class ComponentWrapper {
        Component component;
        Position position;
        Insets padding;

        ComponentWrapper(Component component, Position position, Insets padding) {
            this.component = component;
            this.position = position;
            this.padding = padding;
        }
    }

    private final List<ComponentWrapper> components = new ArrayList<>();

    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof Position) {
            addLayoutComponent(comp, (Position) constraints, new Insets(0, 0, 0, 0));
        } else if (constraints instanceof Object[] && ((Object[]) constraints).length == 2) {
            Object[] arr = (Object[]) constraints;
            if (arr[0] instanceof Position && arr[1] instanceof Insets) {
                addLayoutComponent(comp, (Position) arr[0], (Insets) arr[1]);
            }
        }
    }

    public void addLayoutComponent(Component comp, Position position, Insets padding) {
        components.add(new ComponentWrapper(comp, position, padding));
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int maxWidth = parent.getWidth() - (insets.left + insets.right);
            int maxHeight = parent.getHeight() - (insets.top + insets.bottom);

            for (ComponentWrapper wrapper : components) {
                Component comp = wrapper.component;
                Position pos = wrapper.position;
                Insets padding = wrapper.padding;

                Dimension pref = comp.getPreferredSize();
                int x, y;

                switch (pos) {
                    case CENTER:
                        x = (maxWidth - pref.width) / 2;
                        y = (maxHeight - pref.height) / 2;
                        break;
                    case CENTER_TOP:
                        x = (maxWidth - pref.width) / 2;
                        y = insets.top;
                        break;
                    case CENTER_BOTTOM:
                        x = (maxWidth - pref.width) / 2;
                        y = maxHeight - pref.height;
                        break;
                    case TOP_LEFT:
                        x = insets.left;
                        y = insets.top;
                        break;
                    case TOP_RIGHT:
                        x = maxWidth - pref.width;
                        y = insets.top;
                        break;
                    case BOTTOM_LEFT:
                        x = insets.left;
                        y = maxHeight - pref.height;
                        break;
                    case BOTTOM_RIGHT:
                        x = maxWidth - pref.width;
                        y = maxHeight - pref.height;
                        break;
                    default:
                        x = 0;
                        y = 0;
                }

                comp.setBounds(x + padding.left, y + padding.top,
                        pref.width - (padding.left + padding.right),
                        pref.height - (padding.top + padding.bottom));
            }
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        addLayoutComponent(comp, Position.CENTER, new Insets(0, 0, 0, 0));
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        components.removeIf(wrapper -> wrapper.component == comp);
    }
}
