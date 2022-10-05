package net.tinetwork.tradingcards;

import org.jetbrains.annotations.NotNull;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;
import org.jooq.tools.StringUtils;


/**
 * @author sarhatabaot
 */
public class PrefixNamingStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        String name = replacePrefix(super.getJavaClassName(definition, mode));

        return StringUtils.toUC(name);
    }

    @Override
    public String getJavaIdentifier(final Definition definition) {
        return replacePrefix(super.getJavaIdentifier(definition));
    }


    private @NotNull String replacePrefix(final @NotNull String name) {
        return name.replace("{PREFIX}","").replace("{prefix}","").replace("_7bprefix_7d","").replace("_7bPREFIX_7d","");
    }

}