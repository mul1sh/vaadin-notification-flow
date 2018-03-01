/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.component.notification;

import java.util.Locale;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.HtmlUtils;
import com.vaadin.flow.shared.Registration;

/**
 * Server-side component for the <code>vaadin-notification</code> element.
 *
 * @author Vaadin Ltd
 */
@HtmlImport("frontend://flow-component-renderer.html")
public class Notification extends GeneratedVaadinNotification<Notification>
        implements HasComponents {

    private final Element container = new Element("div", false);
    private final Element templateElement = new Element("template", false);
    private boolean autoAddedToTheUi = false;

    /**
     * Enumeration of all available positions for notification component
     */
    public enum Position {
        TOP_STRETCH, TOP_START, TOP_CENTER, TOP_END, MIDDLE, BOTTOM_START, BOTTOM_CENTER, BOTTOM_END, BOTTOM_STRETCH,;

        private final String clientName;

        Position() {
            this.clientName = name().toLowerCase(Locale.ENGLISH).replace('_',
                    '-');
        }

        /**
         * Gets name that is used in the client side representation of the
         * component.
         *
         * @return the name used in the client side representation of the
         *         component.
         */
        public String getClientName() {
            return clientName;
        }

        /**
         * Creates {@link Position} from the client side representation property
         * name
         * 
         * @param clientName
         *            the client side representation of the property
         * @return corresponding {@link Position}
         */
        static Position fromClientName(String clientName) {
            return Position.valueOf(
                    clientName.replace('-', '_').toUpperCase(Locale.ENGLISH));
        }
    }

    /**
     * Default constructor. Create an empty notification with component support
     * and non-auto-closing
     * <p>
     * Note: To mix text and child components in notification that also supports
     * child components, use the {@link Text} component for the textual parts.
     */
    public Notification() {
        initBaseElementsAndListeners();
        getElement().getNode()
                .runWhenAttached(ui -> ui.beforeClientResponse(this,
                        context -> attachComponentTemplate(ui)));
        setPosition(Position.BOTTOM_START);
        setDuration(0);
    }

    /**
     * Creates a Notification with the given String rendered as its HTML text,
     * that does not close automatically.
     * 
     * @param text
     *            the text of the Notification
     */
    public Notification(String text) {
        this(text, 0, Position.BOTTOM_START);
    }

    /**
     * Creates a Notification with given String rendered as its HTML text and
     * given Integer rendered as its duration.
     * <p>
     * Set to {@code 0} or a negative number to disable the notification
     * auto-closing.
     * 
     * @param text
     *            the text of the Notification
     * @param duration
     *            the duration in milliseconds to show the notification
     */
    public Notification(String text, int duration) {
        this(text, duration, Position.BOTTOM_START);
    }

    /**
     * Creates a Notification with given text String, duration and position
     * <P>
     * Set to {@code 0} or a negative number to disable the notification
     * auto-closing.
     * 
     * @param text
     *            the text of the notification
     * @param duration
     *            the duration in milliseconds to show the notification
     * @param position
     *            the position of the notification. Valid enumerate values are
     *            TOP_STRETCH, TOP_START, TOP_CENTER, TOP_END, MIDDLE,
     *            BOTTOM_START, BOTTOM_CENTER, BOTTOM_END, BOTTOM_STRETCH
     */
    public Notification(String text, int duration, Position position) {
        initBaseElementsAndListeners();
        setText(text);
        setDuration((double) duration);
        setPosition(position);
    }

    /**
     * Creates a notification with given components inside.
     * <p>
     * Note: To mix text and child components in a component that also supports
     * child components, use the {@link Text} component for the textual parts.
     * 
     * @param components
     *            the components inside the notification
     * @see #add(Component...)
     */
    public Notification(Component... components) {
        this();
        add(components);
    }

    private void initBaseElementsAndListeners() {
        getElement().appendChild(templateElement);
        getElement().appendChild(container);

        getElement().addEventListener("opened-changed", event -> {
            if (autoAddedToTheUi && !isOpened()) {
                getElement().removeFromParent();
                autoAddedToTheUi = false;
            }
        });

    }

    /**
     * Shows a notification in the current page with given text, duration and
     * position.
     * 
     * @param text
     *            the text of the Notification
     * @param duration
     *            the duration in milliseconds to show the notification
     * @param position
     *            the position of the notification. Valid enumerate values are
     *            TOP_STRETCH, TOP_START, TOP_CENTER, TOP_END, MIDDLE,
     *            BOTTOM_START, BOTTOM_CENTER, BOTTOM_END, BOTTOM_STRETCH
     * @return The notification
     */
    public static Notification show(String text, int duration,
            Position position) {
        Notification notification = new Notification(text, duration, position);
        notification.open();
        return notification;
    }

    /**
     * Set the text of the notification with given String
     * <p>
     * NOTE: When mixing this method with {@link #Notification()} and
     * {@link #Notification(Component...)}. Method will remove all the
     * components from the notification.
     * 
     * @param text
     *            the text of the Notification
     */
    public void setText(String text) {
        removeAll();
        getElement().getNode().runWhenAttached(
                ui -> ui.beforeClientResponse(this, context -> templateElement
                        .setProperty("innerHTML", HtmlUtils.escape(text))));
    }

    /**
     * Set position of the notification.
     * <P>
     * 
     * @param position
     *            the position of the notification. Valid enumerate values are
     *            {@code TOP_STRETCH, TOP_START, TOP_CENTER, TOP_END, MIDDLE, BOTTOM_START, BOTTOM_CENTER, BOTTOM_END, BOTTOM_STRETCH}
     */
    public void setPosition(Position position) {
        this.setPosition(position.getClientName());
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * Alignment of the notification in the viewport Valid values are
     * {@code top-stretch|top-start|top-center|top-end|middle|bottom-start|bottom-center|bottom-end|bottom-stretch}
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     *
     * @return the {@link Position} property from the webcomponent
     */
    public Position getPosition() {
        return Position.fromClientName(getPositionString());
    }

    /**
     * Opens the notification.
     */
    @Override
    public void open() {
        setOpened(true);
    }

    /**
     * Closes the notification.
     * <p>
     * Note: This method also removes the notification component from the DOM
     * after closing it, unless you have added the component manually.
     */
    @Override
    public void close() {
        setOpened(false);
    }

    /**
     * Adds the given components into this notification.
     * <p>
     * The elements in the DOM will not be children of the
     * {@code <vaadin-notification>} element, but will be inserted into an
     * overlay that is attached into the {@code <body>}.
     * <p>
     * NOTE: When mixing this method with {@link #Notification(String)},
     * {@link #Notification(String, int)} and
     * {@link #Notification(String, int, Position)} method will remove the text
     * content.
     *
     * @param components
     *            the components to add
     */
    @Override
    public void add(Component... components) {
        assert components != null;
        for (Component component : components) {
            assert component != null;
            container.appendChild(component.getElement());
        }
        getElement().getNode()
                .runWhenAttached(ui -> ui.beforeClientResponse(this,
                        context -> attachComponentTemplate(ui)));
    }

    /**
     * Remove the given components from this notification.
     * 
     * @param components
     *            the components to remove
     */
    @Override
    public void remove(Component... components) {
        for (Component component : components) {
            assert component != null;
            if (container.equals(component.getElement().getParent())) {
                container.removeChild(component.getElement());
            } else {
                throw new IllegalArgumentException("The given component ("
                        + component + ") is not a child of this component");
            }
        }
    }

    /**
     * Remove all the components from this notification.
     */
    @Override
    public void removeAll() {
        container.removeAllChildren();
    }

    @Override
    public Stream<Component> getChildren() {
        Builder<Component> childComponents = Stream.builder();
        container.getChildren().forEach(childElement -> ComponentUtil
                .findComponents(childElement, childComponents::add));
        return childComponents.build();
    }

    private void attachComponentTemplate(UI ui) {
        String appId = ui.getInternals().getAppId();
        int nodeId = container.getNode().getId();
        String template = String.format(
                "<flow-component-renderer appid=\"%s\" nodeid=\"%s\"></flow-component-renderer>",
                appId, nodeId);
        templateElement.setProperty("innerHTML", template);
    }

    /**
     * Opens or closes the notification.
     * <p>
     * Note: You don't need to add the component anywhere before opening it.
     * Since {@code <vaadin-notification>}'s location in the DOM doesn't really
     * matter, opening a notification will automatically add it to the
     * {@code <body>} if it's not yet attached anywhere.
     * 
     * @param opened
     *            {@code true} to open the notification, {@code false} to close
     *            it
     */
    @Override
    public void setOpened(boolean opened) {
        UI ui = UI.getCurrent();
        if (opened && getElement().getNode().getParent() == null
                && ui != null) {
            ui.beforeClientResponse(ui, context -> {
                ui.add(this);
                autoAddedToTheUi = true;
            });
        }
        super.setOpened(opened);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * True if the notification is currently displayed.
     * <p>
     * This property is synchronized automatically from client side when a
     * 'opened-changed' event happens.
     * </p>
     *
     * @return the {@code opened} property from the webcomponent
     */
    public boolean isOpened() {
        return isOpenedBoolean();
    }

    @Override
    public Registration addOpenedChangeListener(
            ComponentEventListener<OpenedChangeEvent<Notification>> listener) {
        return super.addOpenedChangeListener(listener);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The duration in milliseconds to show the notification. Set to {@code 0}
     * or a negative number to disable the notification auto-closing.
     * </p>
     *
     * @param duration
     *            the value to set
     */
    public void setDuration(int duration) {
        setDuration((double) duration);
    }

    /**
     * <p>
     * Description copied from corresponding location in WebComponent:
     * </p>
     * <p>
     * The duration in milliseconds to show the notification. Set to {@code 0}
     * or a negative number to disable the notification auto-closing.
     * <p>
     * This property is not synchronized automatically from the client side, so
     * the returned value may not be the same as in client side.
     * </p>
     *
     * @return the {@code duration} property from the webcomponent
     */
    public int getDuration() {
        return (int) getDurationDouble();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: To listen for opening the notification, you should use
     * {@link #addOpenedChangeListener(ComponentEventListener)}.
     */
    @Override
    public Registration addAttachListener(
            ComponentEventListener<AttachEvent> listener) {
        return super.addAttachListener(listener);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: To listen for closing the notification, you should use
     * {@link #addOpenedChangeListener(ComponentEventListener)}, as the
     * component is not necessarily removed from the DOM when closing.
     */
    @Override
    public Registration addDetachListener(
            ComponentEventListener<DetachEvent> listener) {
        return super.addDetachListener(listener);
    }
}
