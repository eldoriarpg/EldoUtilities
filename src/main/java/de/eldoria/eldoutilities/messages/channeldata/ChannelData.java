package de.eldoria.eldoutilities.messages.channeldata;

import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.messages.MessageType;

public interface ChannelData {
    default void localized(ILocalizer localizer, Replacement... replacements) {
    }
    default void forceColor(MessageType type) {
    }
}
