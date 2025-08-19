package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
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
}
