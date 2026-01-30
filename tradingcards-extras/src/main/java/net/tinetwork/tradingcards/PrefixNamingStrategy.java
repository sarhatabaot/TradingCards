package net.tinetwork.tradingcards;

import org.jetbrains.annotations.NotNull;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;
import org.jooq.tools.StringUtils;


/**
 * @author sarhatabaot
 */
// This is needed to ensure prefix placeholders don't stay during code generation.
public class PrefixNamingStrategy extends DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        String original = super.getJavaClassName(definition, mode);
        String replaced = replacePrefix(original);
        return StringUtils.toUC(replaced);
    }

    @Override
    public String getJavaIdentifier(final Definition definition) {
        String original = super.getJavaIdentifier(definition);
        return replacePrefix(original);
    }


    public @NotNull String replacePrefix(final @NotNull String name) {
        return name
                .replaceAll("\\{(TABLEPREFIX|tablePrefix|table_Prefix|TABLE_PREFIX|PREFIX|prefix)}", "")
                .replaceAll("\\$\\{(TABLEPREFIX|tablePrefix|table_Prefix|TABLE_PREFIX|PREFIX|prefix)}", "")
                .replaceAll("_7b(TABLEPREFIX|tablePrefix|table_Prefix|TABLE_PREFIX|PREFIX|prefix)_7d", "")
                .replaceAll("\\{.*?}", "")
                .replaceAll("\\$\\{.*?}", "")
                .replaceAll("_7b.*?_7d", "");
    }


}
