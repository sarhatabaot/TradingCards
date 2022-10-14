package net.tinetwork.tradingcards.api.model.pack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * @author sarhatabaot
 */
public class EmptyPack extends Pack{
    private EmptyPack() {
        super("tc-internal-null-pack", Collections.emptyList(), "", -1.0D, "","tc-internal.null-pack", Collections.emptyList());
    }

    @Contract(" -> new")
    public static @NotNull EmptyPack emptyPack(){
        return new EmptyPack();
    }
}
