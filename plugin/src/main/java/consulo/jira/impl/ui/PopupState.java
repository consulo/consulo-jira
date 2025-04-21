// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package consulo.jira.impl.ui;

import consulo.application.util.registry.Registry;
import consulo.ui.ex.popup.JBPopup;
import consulo.ui.ex.popup.event.JBPopupListener;
import consulo.ui.ex.popup.event.LightweightWindowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

/**
 * This helper class is intended to prevent opening
 * a popup, a popup menu or a balloon right after its closing.
 */
public abstract class PopupState<Popup> {
    private WeakReference<Popup> reference;
    private boolean hiddenLongEnough = true;
    private long timeHiddenAt;

    public static @NotNull PopupState<JBPopup> forPopup() {
        return new JBPopupState();
    }


    public void prepareToShow(@NotNull Popup popup) {
        hidePopup();
        addListener(popup);
        reference = new WeakReference<>(popup);
    }

    public boolean isRecentlyHidden() {
        if (hiddenLongEnough) return false;
        hiddenLongEnough = true;
        return (System.currentTimeMillis() - timeHiddenAt) < Registry.intValue("ide.popup.hide.show.threshold", 200);
    }

    public boolean isHidden() {
        return !isShowing();
    }

    public boolean isShowing() {
        Popup popup = getPopup();
        return popup != null && isShowing(popup);
    }

    public void hidePopup() {
        Popup popup = getPopup();
        if (popup != null) hide(popup);
        reference = null;
    }

    public @Nullable Popup getPopup() {
        WeakReference<Popup> reference = this.reference;
        return reference == null ? null : reference.get();
    }


    abstract void addListener(@NotNull Popup popup);

    abstract void removeListener(@NotNull Popup popup);

    abstract boolean isShowing(@NotNull Popup popup);

    abstract void hide(@NotNull Popup popup);

    void onHide() {
        Popup popup = getPopup();
        if (popup != null) removeListener(popup);
        reference = null;
        hiddenLongEnough = false;
        timeHiddenAt = System.currentTimeMillis();
    }

    private static final class JBPopupState extends PopupState<JBPopup> implements JBPopupListener {
        @Override
        void addListener(@NotNull JBPopup popup) {
            popup.addListener(this);
        }

        @Override
        void removeListener(@NotNull JBPopup popup) {
            popup.removeListener(this);
        }

        @Override
        boolean isShowing(@NotNull JBPopup popup) {
            return popup.isVisible();
        }

        @Override
        void hide(@NotNull JBPopup popup) {
            popup.cancel();
            removeListener(popup);
        }

        @Override
        public void onClosed(@NotNull LightweightWindowEvent event) {
            onHide();
        }
    }
}
