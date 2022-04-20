package org.flareon.alisa.processors;

import org.bukkit.entity.Player;

public interface IProcessor {
    boolean processMessage(final Player player, final String message);
}
