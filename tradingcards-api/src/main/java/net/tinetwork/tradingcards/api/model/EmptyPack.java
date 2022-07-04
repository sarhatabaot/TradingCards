package net.tinetwork.tradingcards.api.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author sarhatabaot
 */
public class EmptyPack extends Pack{
    private EmptyPack() {
        super("tc-internal-null-pack", Collections.emptyList(), "", -1.0D, "","tc-internal.null-pack");
    }

    @Contract(" -> new")
    public static @NotNull EmptyPack emptyPack(){
        return new EmptyPack();
    }
}
