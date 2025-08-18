package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.BaseProviderUIStrategy.copyCommon;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AnthropicParameters extends LanguageModelParameters {
    public Integer topK;
    public Boolean cacheSystemMessages;
    public Boolean cacheTools;
    public String thinkingType;
    public Integer thinkingBudgetTokens;
    public String version;
    public String beta;

    public static AnthropicParameters toAnthropic(LanguageModelParameters base) {
        if (base instanceof AnthropicParameters a) {
            return a;
        }
        AnthropicParameters a = new AnthropicParameters();
        copyCommon(base, a);
        if (a.version == null) a.version = "2023-06-01";
        if (a.topK == null) a.topK = 40;
        if (a.stopSequences == null) a.stopSequences = Collections.emptyList();
        return a;
    }
}
